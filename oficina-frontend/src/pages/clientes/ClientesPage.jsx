import { useState, useEffect, useCallback } from 'react'
import { clienteService } from '../../services/clienteService'
import { usePagination } from '../../hooks/usePagination'
import { Table, Th, Td, EmptyRow } from '../../components/ui/Table'
import { Pagination } from '../../components/ui/Pagination'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'
import { formatCPFCNPJ } from '../../utils/formatters'
import ClienteForm from './ClienteForm'

export default function ClientesPage() {
  const [data, setData] = useState({ content: [], totalPages: 0 })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [search, setSearch] = useState('')
  const [modal, setModal] = useState(null) // null | 'novo' | cliente
  const { page, size, setPage, reset } = usePagination()

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const { data: res } = await clienteService.listar({ page, size, nome: search || undefined })
      setData(res)
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }, [page, size, search])

  useEffect(() => { load() }, [load])

  const handleDelete = async (id) => {
    if (!confirm('Excluir este cliente?')) return
    try {
      await clienteService.excluir(id)
      load()
    } catch (err) {
      setError(err)
    }
  }

  const handleSaved = () => { setModal(null); load() }

  return (
    <div>
      <div className="flex items-center gap-3 mb-5">
        <div>
          <h1 className="text-lg font-medium">Clientes</h1>
          <p className="text-xs text-gray-400 mt-0.5">{data.totalElements ?? 0} cadastrados</p>
        </div>
        <div className="flex-1" />
        <Button variant="primary" onClick={() => setModal('novo')}>+ Novo cliente</Button>
      </div>

      <ApiError error={error} />

      <div className="mb-3">
        <input
          type="text"
          placeholder="Buscar por nome ou documento…"
          value={search}
          onChange={(e) => { setSearch(e.target.value); reset() }}
          className="border border-gray-300 rounded px-3 py-1.5 text-sm w-72 focus:outline-none focus:ring-2 focus:ring-brand-700"
        />
      </div>

      <Table>
        <thead>
          <tr>
            <Th>Nome</Th><Th>Documento</Th><Th>Email</Th><Th>Telefone</Th><Th></Th>
          </tr>
        </thead>
        <tbody>
          {loading && <EmptyRow cols={5} message="Carregando…" />}
          {!loading && data.content.length === 0 && <EmptyRow cols={5} />}
          {!loading && data.content.map((c) => (
            <tr key={c.id} className="hover:bg-gray-50">
              <Td className="font-medium">{c.nome}</Td>
              <Td className="font-mono text-xs">{formatCPFCNPJ(c.doc)}</Td>
              <Td>{c.email}</Td>
              <Td>{c.telefone}</Td>
              <Td>
                <div className="flex gap-2">
                  <Button size="xs" onClick={() => setModal(c)}>Editar</Button>
                  <Button size="xs" variant="danger" onClick={() => handleDelete(c.id)}>Excluir</Button>
                </div>
              </Td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />

      {modal && (
        <ClienteForm
          cliente={modal === 'novo' ? null : modal}
          onClose={() => setModal(null)}
          onSaved={handleSaved}
        />
      )}
    </div>
  )
}
