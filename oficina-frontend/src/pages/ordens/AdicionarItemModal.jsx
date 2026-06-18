import { useState, useEffect } from 'react'
import { produtoService } from '../../services/produtoService'
import { servicoService } from '../../services/servicoService'
import { ordemService } from '../../services/ordemService'
import { Modal } from '../../components/ui/Modal'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'
import { formatBRL } from '../../utils/formatters'

export default function AdicionarItemModal({ ordemId, onClose, onSaved }) {
  const [tipo, setTipo] = useState('PRODUTO')
  const [produtos, setProdutos] = useState([])
  const [servicos, setServicos] = useState([])
  const [selectedId, setSelectedId] = useState('')
  const [quantidade, setQuantidade] = useState(1)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    produtoService.listar({ size: 100 }).then((r) => {
      const list = r.data.content ?? r.data
      setProdutos(list)
      if (list.length) setSelectedId(list[0].id)
    })
    servicoService.listar({ size: 100 }).then((r) => {
      setServicos(r.data.content ?? r.data)
    })
  }, [])

  useEffect(() => {
    const list = tipo === 'PRODUTO' ? produtos : servicos
    if (list.length) setSelectedId(list[0].id)
  }, [tipo, produtos, servicos])

  const items = tipo === 'PRODUTO' ? produtos : servicos

  const handleSave = async () => {
    setLoading(true); setError(null)
    try {
      if (tipo === 'PRODUTO') {
        await ordemService.adicionarProduto(ordemId, { produtoId: selectedId, quantidade })
      } else {
        await ordemService.adicionarServico(ordemId, { servicoId: selectedId })
      }
      onSaved()
    } catch (err) { setError(err) }
    finally { setLoading(false) }
  }

  const selected = items.find((i) => String(i.id) === String(selectedId))

  return (
    <Modal title="Adicionar item à OS" onClose={onClose}>
      <ApiError error={error} />
      <div className="flex flex-col gap-4">
        <div className="flex gap-2">
          {['PRODUTO', 'SERVICO'].map((t) => (
            <button
              key={t}
              onClick={() => setTipo(t)}
              className={`flex-1 py-1.5 text-sm border rounded transition-colors ${
                tipo === t ? 'bg-green-900 text-white border-green-900' : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'
              }`}
            >
              {t === 'PRODUTO' ? 'Produto' : 'Serviço'}
            </button>
          ))}
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs text-gray-500 font-mono">Item</label>
          <select
            value={selectedId}
            onChange={(e) => setSelectedId(e.target.value)}
            className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-700"
          >
            {items.map((i) => (
              <option key={i.id} value={i.id}>{i.nome} — {formatBRL(i.preco)}</option>
            ))}
          </select>
        </div>

        {tipo === 'PRODUTO' && (
          <div className="flex flex-col gap-1">
            <label className="text-xs text-gray-500 font-mono">Quantidade</label>
            <input
              type="number" min="1" value={quantidade}
              onChange={(e) => setQuantidade(parseInt(e.target.value))}
              className="border border-gray-300 rounded px-3 py-1.5 text-sm w-28 focus:outline-none focus:ring-2 focus:ring-green-700"
            />
          </div>
        )}

        {selected && (
          <div className="bg-gray-50 rounded-lg px-4 py-2.5 text-sm text-gray-600">
            Subtotal estimado: <span className="font-mono font-medium">
              {formatBRL(selected.preco * (tipo === 'PRODUTO' ? quantidade : 1))}
            </span>
          </div>
        )}

        <div className="flex gap-2 justify-end">
          <Button onClick={onClose}>Cancelar</Button>
          <Button variant="primary" loading={loading} onClick={handleSave}>Adicionar</Button>
        </div>
      </div>
    </Modal>
  )
}
