import { useState, useEffect, useCallback } from 'react'
import { authService } from '../../services/authService'
import { Table, Th, Td, EmptyRow } from '../../components/ui/Table'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'
import { Modal } from '../../components/ui/Modal'
import { Input, Select } from '../../components/ui/Input'
import { TIPO_USUARIO } from '../../utils/constants'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import api from '../../services/api'

const schema = z.object({
  nome: z.string().min(2, 'Obrigatório'),
  email: z.string().email('Email inválido'),
  senha: z.string().min(6, 'Mínimo 6 caracteres'),
  role: z.enum(['ADMIN', 'ATENDENTE', 'MECANICO']),
})

function NovoUsuarioModal({ onClose, onSaved }) {
  const [apiError, setApiError] = useState(null)
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: { role: 'ATENDENTE' },
  })

  const onSubmit = async ({ nome, email, senha, role }) => {
    setApiError(null)
    try {
      await authService.cadastrar({ nome, email, senha, tipo: role })
      onSaved()
    } catch (err) { setApiError(err) }
  }

  return (
    <Modal title="Cadastrar usuário" onClose={onClose}>
      <ApiError error={apiError} />
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
        <Input label="Nome" {...register('nome')} error={errors.nome?.message} />
        <Input label="Email" type="email" {...register('email')} error={errors.email?.message} />
        <div className="grid grid-cols-2 gap-3">
          <Input label="Senha" type="password" {...register('senha')} error={errors.senha?.message} />
          <Select label="Perfil" {...register('role')} error={errors.role?.message}>
            {TIPO_USUARIO.map((r) => <option key={r}>{r}</option>)}
          </Select>
        </div>
        <div className="flex gap-2 justify-end">
          <Button type="button" onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="primary" loading={isSubmitting}>Cadastrar</Button>
        </div>
      </form>
    </Modal>
  )
}

export default function UsuariosPage() {
  const [usuarios, setUsuarios] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [showModal, setShowModal] = useState(false)

  const load = useCallback(async () => {
    setLoading(true); setError(null)
    try {
      const { data } = await api.get('/usuarios')
      setUsuarios(Array.isArray(data) ? data : data.content ?? [])
    } catch (err) { setError(err) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { load() }, [load])

  const ROLE_COLOR = {
    ADMIN: 'bg-red-100 text-red-700',
    ATENDENTE: 'bg-blue-100 text-blue-700',
    MECANICO: 'bg-green-100 text-green-700',
  }

  return (
    <div>
      <div className="flex items-center gap-3 mb-5">
        <div>
          <h1 className="text-lg font-medium">Usuários</h1>
          <p className="text-xs text-gray-400 mt-0.5">{usuarios.length} cadastrados</p>
        </div>
        <div className="flex-1" />
        <Button variant="primary" onClick={() => setShowModal(true)}>+ Novo usuário</Button>
      </div>

      <ApiError error={error} />

      <Table>
        <thead>
          <tr><Th>Nome</Th><Th>Email</Th><Th>Perfil</Th></tr>
        </thead>
        <tbody>
          {loading && <EmptyRow cols={3} message="Carregando…" />}
          {!loading && usuarios.length === 0 && <EmptyRow cols={3} />}
          {!loading && usuarios.map((u) => (
            <tr key={u.id} className="hover:bg-gray-50">
              <Td className="font-medium">{u.nome}</Td>
              <Td className="text-gray-500">{u.email}</Td>
              <Td>
                <span className={`text-xs font-mono px-2 py-0.5 rounded ${ROLE_COLOR[u.tipo] ?? 'bg-gray-100 text-gray-600'}`}>
                  {u.tipo}
                </span>
              </Td>
            </tr>
          ))}
        </tbody>
      </Table>

      {showModal && (
        <NovoUsuarioModal
          onClose={() => setShowModal(false)}
          onSaved={() => { setShowModal(false); load() }}
        />
      )}
    </div>
  )
}