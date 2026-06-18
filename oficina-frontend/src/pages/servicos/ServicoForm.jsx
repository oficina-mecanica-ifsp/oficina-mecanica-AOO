import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useState } from 'react'
import { servicoService } from '../../services/servicoService'
import { Modal } from '../../components/ui/Modal'
import { Input } from '../../components/ui/Input'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'
import { buildDuration } from '../../utils/formatters'

const parsePT = (iso = '') => {
  const m = iso.match(/PT(?:(\d+)H)?(?:(\d+)M)?/)
  return { horas: m?.[1] ?? '0', minutos: m?.[2] ?? '0' }
}

const schema = z.object({
  nome: z.string().min(2, 'Obrigatório'),
  preco: z.coerce.number().positive('Preço inválido'),
  horas: z.coerce.number().min(0).max(99),
  minutos: z.coerce.number().min(0).max(59),
})

export default function ServicoForm({ servico, onClose, onSaved }) {
  const [apiError, setApiError] = useState(null)
  const duracao = servico ? parsePT(servico.duracao) : { horas: '0', minutos: '0' }

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: { nome: servico?.nome ?? '', preco: servico?.preco ?? '', ...duracao },
  })

  const onSubmit = async ({ nome, preco, horas, minutos }) => {
    setApiError(null)
    const duracao = buildDuration(horas, minutos)
    try {
      if (servico) await servicoService.atualizar(servico.id, { nome, preco, duracao })
      else await servicoService.criar({ nome, preco, duracao })
      onSaved()
    } catch (err) { setApiError(err) }
  }

  return (
    <Modal title={servico ? 'Editar serviço' : 'Novo serviço'} onClose={onClose}>
      <ApiError error={apiError} />
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
        <Input label="Nome do serviço" {...register('nome')} error={errors.nome?.message} />
        <Input label="Preço (R$)" type="number" step="0.01" {...register('preco')} error={errors.preco?.message} />
        <div className="flex flex-col gap-1">
          <label className="text-xs text-gray-500 font-mono">Duração</label>
          <div className="flex gap-3 items-center">
            <div className="flex items-center gap-1.5">
              <input type="number" min="0" max="99" {...register('horas')}
                className="border border-gray-300 rounded px-3 py-1.5 text-sm w-20 focus:outline-none focus:ring-2 focus:ring-green-700" />
              <span className="text-sm text-gray-500">h</span>
            </div>
            <div className="flex items-center gap-1.5">
              <input type="number" min="0" max="59" step="15" {...register('minutos')}
                className="border border-gray-300 rounded px-3 py-1.5 text-sm w-20 focus:outline-none focus:ring-2 focus:ring-green-700" />
              <span className="text-sm text-gray-500">min</span>
            </div>
          </div>
        </div>
        <div className="flex gap-2 justify-end">
          <Button type="button" onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="primary" loading={isSubmitting}>Salvar</Button>
        </div>
      </form>
    </Modal>
  )
}
