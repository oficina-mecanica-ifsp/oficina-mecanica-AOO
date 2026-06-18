import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useState } from 'react'
import { produtoService } from '../../services/produtoService'
import { Modal } from '../../components/ui/Modal'
import { Input, Select } from '../../components/ui/Input'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'
import { TIPO_PRODUTO } from '../../utils/constants'

const schema = z.object({
  nome: z.string().min(2, 'Obrigatório'),
  tipo: z.enum(['PECA', 'PRODUTO']),
  preco: z.coerce.number().positive('Preço inválido'),
  quantidade: z.coerce.number().min(0),
  qtdMinima: z.coerce.number().min(0),
})

export default function ProdutoForm({ produto, onClose, onSaved }) {
  const [apiError, setApiError] = useState(null)
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: produto ?? { quantidade: 0, qtdMinima: 5 },
  })

  const onSubmit = async (dados) => {
    setApiError(null)
    try {
      if (produto) await produtoService.atualizar(produto.id, dados)
      else await produtoService.criar(dados)
      onSaved()
    } catch (err) { setApiError(err) }
  }

  return (
    <Modal title={produto ? 'Editar produto' : 'Novo produto'} onClose={onClose}>
      <ApiError error={apiError} />
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
        <Input label="Nome" {...register('nome')} error={errors.nome?.message} />
        <div className="grid grid-cols-2 gap-3">
          <Select label="Tipo" {...register('tipo')} error={errors.tipo?.message}>
            {TIPO_PRODUTO.map((t) => <option key={t}>{t}</option>)}
          </Select>
          <Input label="Preço (R$)" type="number" step="0.01" {...register('preco')} error={errors.preco?.message} />
          <Input label="Quantidade" type="number" {...register('quantidade')} error={errors.quantidade?.message} />
          <Input label="Qtd mínima" type="number" {...register('qtdMinima')} error={errors.qtdMinima?.message} />
        </div>
        <div className="flex gap-2 justify-end">
          <Button type="button" onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="primary" loading={isSubmitting}>Salvar</Button>
        </div>
      </form>
    </Modal>
  )
}
