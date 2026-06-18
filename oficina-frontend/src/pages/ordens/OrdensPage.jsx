import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { ordemService } from '../../services/ordemService'
import { usePagination } from '../../hooks/usePagination'
import { useAuth } from '../../hooks/useAuth'
import { Table, Th, Td, EmptyRow } from '../../components/ui/Table'
import { Pagination } from '../../components/ui/Pagination'
import { Button } from '../../components/ui/Button'
import { Badge } from '../../components/ui/Badge'
import { ApiError } from '../../components/ui/Alert'
import { formatBRL, formatDate } from '../../utils/formatters'
import { STATUS_LABELS } from '../../utils/constants'
import OrdemForm from './OrdemForm'

export default function OrdensPage() {
  const { can } = useAuth()
  const [data, setData] = useState({ content: [], totalPages: 0, totalElements: 0 })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [filterStatus, setFilterStatus] = useState('')
  const [showForm, setShowForm] = useState(false)
  const { page, size, setPage, reset } = usePagination()

  const load = useCallback(async () => {
    setLoading(true); setError(null)
    try {
      const params = { page, size }
      if (filterStatus) params.status = filterStatus
      const { data: res } = await ordemService.listar(params)
      setData(res)
    } catch (err) { setError(err) }
    finally { setLoading(false) }
  }, [page, size, filterStatus])

  useEffect(() => { load() }, [load])

  return (
    <div>
      <div className="flex items-center gap-3 mb-5">
        <div>
          <h1 className="text-lg font-medium">Ordens de Serviço</h1>
          <p className="text-xs text-gray-400 mt-0.5">{data.totalElements} registros</p>
        </div>
        <div className="flex-1" />
        {can(['ADMIN', 'ATENDENTE']) && (
          <Button variant="primary" onClick={() => setShowForm(true)}>+ Nova OS</Button>
        )}
      </div>

      <ApiError error={error} />

      <div className="mb-3">
        <select
          value={filterStatus}
          onChange={(e) => { setFilterStatus(e.target.value); reset() }}
          className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-700"
        >
          <option value="">Todos os status</option>
          {Object.entries(STATUS_LABELS).map(([k, v]) => (
            <option key={k} value={k}>{v}</option>
          ))}
        </select>
      </div>

      <Table>
        <thead>
          <tr>
            <Th>#</Th><Th>Status</Th><Th>Cliente</Th><Th>Veículo</Th>
            <Th>Total</Th><Th>Data</Th><Th></Th>
          </tr>
        </thead>
        <tbody>
          {loading && <EmptyRow cols={7} message="Carregando…" />}
          {!loading && data.content.length === 0 && <EmptyRow cols={7} message="Nenhuma OS encontrada." />}
          {!loading && data.content.map((o) => (
            <tr key={o.id} className="hover:bg-gray-50">
              <Td className="font-mono text-gray-400">#{String(o.id).padStart(4, '0')}</Td>
              <Td><Badge status={o.status} /></Td>
              <Td>{o.clienteNome ?? '—'}</Td>
              <Td className="font-mono text-xs">{o.carroPlaca ?? 'Não vinculado'}</Td>
              <Td className="font-mono">{formatBRL(o.total)}</Td>
              <Td className="text-xs text-gray-400">{formatDate(o.data)}</Td>
              <Td>
                <Link to={`/ordens/${o.id}`}>
                  <Button size="xs">Ver</Button>
                </Link>
              </Td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />

      {showForm && (
        <OrdemForm
          onClose={() => setShowForm(false)}
          onSaved={() => { setShowForm(false); load() }}
        />
      )}
    </div>
  )
}
