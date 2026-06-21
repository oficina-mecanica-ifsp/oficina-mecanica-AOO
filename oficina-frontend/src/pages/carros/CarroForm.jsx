import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useState } from 'react'
import { clienteService } from '../../services/clienteService'
import { carroService } from '../../services/carroService'
import { Modal } from '../../components/ui/Modal'
import { Input } from '../../components/ui/Input'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'

const schema = z.object({
  placa: z.string().regex(/^[A-Z]{3}-\d{4}$/, 'Formato: ABC-1234'),
  modelo: z.string().min(1, 'Obrigatório'),
  marca: z.string().min(1, 'Obrigatório'),
  anoFabricacao: z.coerce.number().min(1950).max(new Date().getFullYear() + 1),
  cor: z.string().min(1, 'Obrigatório'),
  clienteDoc: z.string().regex(/^\d{11}$|^\d{14}$/, 'CPF ou CNPJ do cliente'),
})

export default function CarroForm({ carro, onClose, onSaved }) {
  const [apiError, setApiError] = useState(null)
  const [clienteNome, setClienteNome] = useState(carro?.clienteNome ?? '')
  const [clienteId, setClienteId] = useState(carro?.clienteId ?? null)

  const { register, handleSubmit, formState: { errors, isSubmitting }, setValue, watch } = useForm({
    resolver: zodResolver(schema),
    defaultValues: { ...carro, clienteDoc: carro?.clienteDoc ?? '' },
  })

  const buscarCliente = async (doc) => {
    const clean = doc.replace(/\D/g, '')
    if (clean.length !== 11 && clean.length !== 14) { setClienteNome(''); setClienteId(null); return }
    try {
      const { data } = await clienteService.buscarPorDoc(clean)
      setClienteNome(data.nome)
      setClienteId(data.id)
    } catch { setClienteNome('Cliente não encontrado'); setClienteId(null) }
  }

  const onSubmit = async (dados) => {
    setApiError(null)
    if (!clienteId) {
      setApiError({ response: { data: { message: 'Cliente não encontrado para o CPF/CNPJ informado' } } })
      return
    }
    try {
      const { clienteDoc, ...rest } = dados
      const payload = { ...rest, clienteId }
      if (carro) await carroService.atualizar(carro.id, payload)
      else await carroService.criar(payload)
      onSaved()
    } catch (err) { setApiError(err) }
  }

  return (
    <Modal title={carro ? 'Editar veículo' : 'Novo veículo'} onClose={onClose}>
      <ApiError error={apiError} />
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Placa (ABC-1234)"
            {...register('placa')}
            error={errors.placa?.message}
            onChange={(e) => setValue('placa', e.target.value.toUpperCase())}
            maxLength={8}
          />
          <Input label="Ano de Fabricação" type="number" {...register('anoFabricacao')} error={errors.anoFabricacao?.message} />
          <Input label="Modelo" {...register('modelo')} error={errors.modelo?.message} />
          <Input label="Marca" {...register('marca')} error={errors.marca?.message} />
          <Input label="Cor" {...register('cor')} error={errors.cor?.message} />
        </div>
        <div className="flex flex-col gap-1">
          <Input
            label="CPF/CNPJ do cliente (só números)"
            {...register('clienteDoc')}
            error={errors.clienteDoc?.message}
            onBlur={(e) => buscarCliente(e.target.value)}
            maxLength={14}
          />
          {clienteNome && (
            <p className="text-xs text-brand-700 font-mono mt-0.5">{clienteNome}</p>
          )}
        </div>
        <div className="flex gap-2 justify-end mt-2">
          <Button type="button" onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="primary" loading={isSubmitting}>Salvar</Button>
        </div>
      </form>
    </Modal>
  )
}
