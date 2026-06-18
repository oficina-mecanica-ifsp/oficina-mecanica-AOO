import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useState } from 'react'
import { clienteService } from '../../services/clienteService'
import { Modal } from '../../components/ui/Modal'
import { Input } from '../../components/ui/Input'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'

const schema = z.object({
  nome: z.string().min(3, 'Mínimo 3 caracteres').max(100, 'Máximo 100 caracteres'),
  doc: z.string().regex(/^\d{11}$|^\d{14}$/, 'CPF (11 dígitos) ou CNPJ (14 dígitos)'),
  email: z.string().email('Email inválido'),
  telefone: z.string().regex(/^\d{10,11}$/, '10 ou 11 dígitos numéricos'),
})

export default function ClienteForm({ cliente, onClose, onSaved }) {
  const [apiError, setApiError] = useState(null)
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: cliente ?? {},
  })

  const onSubmit = async (dados) => {
    setApiError(null)
    try {
      if (cliente) await clienteService.atualizar(cliente.id, dados)
      else await clienteService.criar(dados)
      onSaved()
    } catch (err) {
      setApiError(err)
    }
  }

  return (
    <Modal title={cliente ? 'Editar cliente' : 'Novo cliente'} onClose={onClose}>
      <ApiError error={apiError} />
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
        <Input label="Nome completo" {...register('nome')} error={errors.nome?.message} />
        <div className="grid grid-cols-2 gap-3">
          <Input label="CPF ou CNPJ (só números)" {...register('doc')} error={errors.doc?.message} maxLength={14} />
          <Input label="Telefone (10–11 dígitos)" {...register('telefone')} error={errors.telefone?.message} maxLength={11} />
        </div>
        <Input label="Email" type="email" {...register('email')} error={errors.email?.message} />
        <div className="flex gap-2 justify-end mt-2">
          <Button type="button" onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="primary" loading={isSubmitting}>Salvar</Button>
        </div>
      </form>
    </Modal>
  )
}
