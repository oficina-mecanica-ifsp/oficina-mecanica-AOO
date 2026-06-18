import { useState } from 'react'
import { produtoService } from '../../services/produtoService'
import { Modal } from '../../components/ui/Modal'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'

export default function EntradaEstoqueModal({ produto, onClose, onSaved }) {
  const [quantidade, setQuantidade] = useState(10)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleSave = async () => {
    setLoading(true); setError(null)
    try {
      await produtoService.entradaEstoque(produto.id, quantidade)
      onSaved()
    } catch (err) { setError(err) }
    finally { setLoading(false) }
  }

  return (
    <Modal title={`Entrada de estoque — ${produto.nome}`} onClose={onClose}>
      <ApiError error={error} />
      <p className="text-sm text-gray-500 mb-4">
        Estoque atual: <strong>{produto.quantidade}</strong> unidades
        {produto.quantidade < produto.qtdMinima && (
          <span className="ml-2 text-red-600 text-xs">(abaixo do mínimo de {produto.qtdMinima})</span>
        )}
      </p>
      <div className="flex flex-col gap-1 mb-4">
        <label className="text-xs text-gray-500 font-mono">Quantidade a adicionar</label>
        <input
          type="number" min="1" value={quantidade}
          onChange={(e) => setQuantidade(parseInt(e.target.value))}
          className="border border-gray-300 rounded px-3 py-1.5 text-sm w-32 focus:outline-none focus:ring-2 focus:ring-green-700"
        />
      </div>
      <div className="flex gap-2 justify-end">
        <Button onClick={onClose}>Cancelar</Button>
        <Button variant="primary" loading={loading} onClick={handleSave}>Confirmar entrada</Button>
      </div>
    </Modal>
  )
}
