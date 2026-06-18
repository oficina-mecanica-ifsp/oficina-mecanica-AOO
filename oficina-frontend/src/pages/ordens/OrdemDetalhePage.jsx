import { useState, useEffect, useCallback } from 'react'
import { useParams, Link } from 'react-router-dom'
import { ordemService } from '../../services/ordemService'
import { pagamentoService } from '../../services/pagamentoService'
import { useAuth } from '../../hooks/useAuth'
import { Badge } from '../../components/ui/Badge'
import { Button } from '../../components/ui/Button'
import { Modal } from '../../components/ui/Modal'
import { ApiError, Alert } from '../../components/ui/Alert'
import { formatBRL, formatDate, formatCPFCNPJ } from '../../utils/formatters'
import {
  ORDER_STATUS_TRANSITIONS, STATUS_LABELS, STATUS_ORDER, FORMAS_PAGAMENTO,
} from '../../utils/constants'
import AdicionarItemModal from './AdicionarItemModal'

export default function OrdemDetalhePage() {
  const { id } = useParams()
  const { can } = useAuth()
  const [ordem, setOrdem] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [statusModal, setStatusModal] = useState(false)
  const [pagModal, setPagModal] = useState(false)
  const [itemModal, setItemModal] = useState(false)

  const load = useCallback(async () => {
    setLoading(true); setError(null)
    try { const { data } = await ordemService.buscarPorId(id); setOrdem(data) }
    catch (err) { setError(err) }
    finally { setLoading(false) }
  }, [id])

  useEffect(() => { load() }, [load])

  if (loading) return <p className="text-sm text-gray-400 p-6">Carregando…</p>
  if (error) return <ApiError error={error} />
  if (!ordem) return null

  const nexts = ORDER_STATUS_TRANSITIONS[ordem.status] ?? []
  const canStatus = can(['ADMIN', 'MECANICO'])
  const canEdit = can(['ADMIN', 'ATENDENTE'])
  const canItem = can(['ADMIN', 'MECANICO'])

  const pipeIdx = STATUS_ORDER.indexOf(ordem.status)

  const handleRemoverItem = async (tipo, itemId) => {
    if (!confirm('Remover este item?')) return
    try {
      if (tipo === 'PRODUTO') await ordemService.removerProduto(id, itemId)
      else await ordemService.removerServico(id, itemId)
      load()
    } catch (err) { setError(err) }
  }

  return (
    <div>
      <div className="text-xs text-gray-400 font-mono mb-4 flex items-center gap-2">
        <Link to="/ordens" className="text-green-700 hover:underline">← Ordens</Link>
        <span>/</span>
        <span>#{String(ordem.id).padStart(4, '0')}</span>
      </div>

      <div className="flex items-start gap-3 mb-5">
        <div>
          <div className="flex items-center gap-3">
            <h1 className="text-lg font-medium">OS #{String(ordem.id).padStart(4, '0')}</h1>
            <Badge status={ordem.status} />
          </div>
          <p className="text-xs text-gray-400 mt-1">Aberta em {formatDate(ordem.data)}</p>
        </div>
        <div className="flex-1" />
        {canStatus && nexts.length > 0 && (
          <Button onClick={() => setStatusModal(true)}>Avançar status →</Button>
        )}
      </div>

      {/* Pipeline */}
      <div className="flex mb-5 rounded-xl overflow-hidden border border-gray-100">
        {STATUS_ORDER.map((s, i) => (
          <div
            key={s}
            className={`flex-1 text-center py-2 text-[11px] font-mono border-r border-gray-100 last:border-0 ${
              i < pipeIdx ? 'bg-green-50 text-green-700'
              : i === pipeIdx ? 'bg-green-900 text-white font-medium'
              : 'bg-gray-50 text-gray-400'
            }`}
          >
            {STATUS_LABELS[s]}
          </div>
        ))}
      </div>

      {/* Cliente + Veículo */}
      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="bg-white border border-gray-100 rounded-xl p-4">
          <p className="text-[10px] font-mono text-gray-400 uppercase tracking-wider mb-3">Cliente</p>
          {[
            ['Nome', ordem.clienteNome],
            ['Documento', ordem.clienteDoc ? formatCPFCNPJ(ordem.clienteDoc) : '—'],
            ['Email', ordem.clienteEmail],
            ['Tel.', ordem.clienteTelefone],
          ].map(([k, v]) => (
            <div key={k} className="flex gap-2 text-sm mb-1.5">
              <span className="text-gray-400 w-20 flex-shrink-0">{k}</span>
              <span className="font-mono text-xs">{v ?? '—'}</span>
            </div>
          ))}
        </div>
        <div className="bg-white border border-gray-100 rounded-xl p-4">
          <p className="text-[10px] font-mono text-gray-400 uppercase tracking-wider mb-3">Veículo</p>
          {ordem.carroPlaca ? (
            [
              ['Placa', ordem.carroPlaca],
              ['Modelo', `${ordem.carroModelo} ${ordem.carroMarca}`],
              ['Ano', ordem.carroAno],
              ['Cor', ordem.carroCor],
            ].map(([k, v]) => (
              <div key={k} className="flex gap-2 text-sm mb-1.5">
                <span className="text-gray-400 w-20 flex-shrink-0">{k}</span>
                <span className="font-mono text-xs font-medium">{v ?? '—'}</span>
              </div>
            ))
          ) : (
            <p className="text-sm text-gray-400">Nenhum veículo vinculado.</p>
          )}
        </div>
      </div>

      {/* Itens */}
      <div className="bg-white border border-gray-100 rounded-xl overflow-hidden mb-4">
        <div className="flex items-center px-4 py-3 border-b border-gray-100 gap-2">
          <span className="font-medium text-sm">Itens da OS</span>
          <div className="flex-1" />
          {canItem && (
            <Button size="sm" onClick={() => setItemModal(true)}>+ Adicionar item</Button>
          )}
        </div>
        <table className="w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-2 text-left text-xs font-mono text-gray-400">Tipo</th>
              <th className="px-4 py-2 text-left text-xs font-mono text-gray-400">Nome</th>
              <th className="px-4 py-2 text-right text-xs font-mono text-gray-400">Preço unit.</th>
              <th className="px-4 py-2 text-center text-xs font-mono text-gray-400">Qtd</th>
              <th className="px-4 py-2 text-right text-xs font-mono text-gray-400">Subtotal</th>
              {canItem && <th className="px-4 py-2"></th>}
            </tr>
          </thead>
          <tbody>
            {(ordem.itens ?? []).length === 0 && (
              <tr key="empty"><td colSpan={6} className="px-4 py-6 text-center text-gray-400 text-sm">Nenhum item adicionado.</td></tr>
            )}
            {(ordem.itens ?? []).map((item) => (
              <tr key={item.id} className="border-t border-gray-50 hover:bg-gray-50">
                <td className="px-4 py-2.5">
                  <span className={`text-xs font-mono px-2 py-0.5 rounded ${item.tipo === 'SERVICO' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'}`}>
                    {item.tipo}
                  </span>
                </td>
                <td className="px-4 py-2.5">{item.nome}</td>
                <td className="px-4 py-2.5 text-right font-mono">{formatBRL(item.preco)}</td>
                <td className="px-4 py-2.5 text-center">{item.qtd}</td>
                <td className="px-4 py-2.5 text-right font-mono font-medium">{formatBRL(item.preco * item.qtd)}</td>
                {canItem && (
                  <td className="px-4 py-2.5">
                    <Button size="xs" variant="danger" onClick={() => handleRemoverItem(item.tipo, item.id)}>✕</Button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
          <tfoot className="border-t border-gray-200">
            <tr>
              <td colSpan={canItem ? 5 : 4} className="px-4 py-2.5 text-right font-medium text-sm text-gray-600">Total</td>
              <td className="px-4 py-2.5 text-right font-mono font-medium">{formatBRL(ordem.total)}</td>
              {canItem && <td />}
            </tr>
          </tfoot>
        </table>
      </div>

      {/* Pagamento */}
      <div className="bg-white border border-gray-100 rounded-xl p-4">
        <p className="font-medium text-sm mb-3">Pagamento</p>
        {ordem.pagamento ? (
          <Alert type="success">
            ✓ Pago: {formatBRL(ordem.pagamento.valor)} via {ordem.pagamento.forma} em {formatDate(ordem.pagamento.data)}
          </Alert>
        ) : canEdit ? (
          <Button onClick={() => setPagModal(true)}>Registrar pagamento</Button>
        ) : (
          <p className="text-sm text-gray-400">Sem pagamento registrado.</p>
        )}
      </div>

      {statusModal && (
        <StatusModal ordem={ordem} onClose={() => setStatusModal(false)} onSaved={load} />
      )}
      {pagModal && (
        <PagamentoModal ordem={ordem} onClose={() => setPagModal(false)} onSaved={() => { setPagModal(false); load() }} />
      )}
      {itemModal && (
        <AdicionarItemModal ordemId={id} onClose={() => setItemModal(false)} onSaved={() => { setItemModal(false); load() }} />
      )}
    </div>
  )
}

function StatusModal({ ordem, onClose, onSaved }) {
  const nexts = ORDER_STATUS_TRANSITIONS[ordem.status] ?? []
  const [status, setStatus] = useState(nexts[0] ?? '')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleSave = async () => {
    setLoading(true); setError(null)
    try {
      await ordemService.avancarStatus(ordem.id, status)
      onSaved(); onClose()
    } catch (err) { setError(err) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Avançar status" onClose={onClose}>
      <ApiError error={error} />
      <p className="text-sm text-gray-500 mb-4">Status atual: <Badge status={ordem.status} /></p>
      <div className="flex flex-col gap-4">
        <div className="flex flex-col gap-1">
          <label className="text-xs text-gray-500 font-mono">Próximo status</label>
          <select value={status} onChange={(e) => setStatus(e.target.value)}
            className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-700">
            {nexts.map((s) => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
          </select>
        </div>
        <div className="flex gap-2 justify-end">
          <Button onClick={onClose}>Cancelar</Button>
          <Button variant="primary" loading={loading} onClick={handleSave}>Confirmar</Button>
        </div>
      </div>
    </Modal>
  )
}

function PagamentoModal({ ordem, onClose, onSaved }) {
  const [valor, setValor] = useState(ordem.total)
  const [forma, setForma] = useState(FORMAS_PAGAMENTO[0])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const handleSave = async () => {
    setLoading(true); setError(null)
    try {
      await pagamentoService.registrar({ orderId: ordem.id, valor, formaPagamento: forma })
      onSaved()
    } catch (err) { setError(err) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Registrar pagamento" onClose={onClose}>
      <ApiError error={error} />
      <div className="flex flex-col gap-4">
        <div className="grid grid-cols-2 gap-3">
          <div className="flex flex-col gap-1">
            <label className="text-xs text-gray-500 font-mono">Valor (R$)</label>
            <input type="number" step="0.01" value={valor} onChange={(e) => setValor(parseFloat(e.target.value))}
              className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-700" />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-xs text-gray-500 font-mono">Forma de pagamento</label>
            <select value={forma} onChange={(e) => setForma(e.target.value)}
              className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-700">
              {FORMAS_PAGAMENTO.map((f) => <option key={f}>{f}</option>)}
            </select>
          </div>
        </div>
        <div className="flex gap-2 justify-end">
          <Button onClick={onClose}>Cancelar</Button>
          <Button variant="primary" loading={loading} onClick={handleSave}>Confirmar</Button>
        </div>
      </div>
    </Modal>
  )
}