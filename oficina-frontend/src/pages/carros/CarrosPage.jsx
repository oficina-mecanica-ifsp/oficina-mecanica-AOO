import { useState, useEffect, useCallback } from 'react'
import { carroService } from '../../services/carroService'
import { usePagination } from '../../hooks/usePagination'
import { Table, Th, Td, EmptyRow } from '../../components/ui/Table'
import { Pagination } from '../../components/ui/Pagination'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'
import CarroForm from './CarroForm'

export default function CarrosPage() {
  const [data, setData] = useState({ content: [], totalPages: 0 })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [modal, setModal] = useState(null)
  const { page, size, setPage } = usePagination()

  const load = useCallback(async () => {
    setLoading(true); setError(null)
    try {
      const { data: res } = await carroService.listar({ page, size })
      setData(res)
    } catch (err) { setError(err) }
    finally { setLoading(false) }
  }, [page, size])

  useEffect(() => { load() }, [load])

  const handleDelete = async (id) => {
    if (!confirm('Excluir este veículo?')) return
    try { await carroService.excluir(id); load() }
    catch (err) { setError(err) }
  }

  return (
    <div>
      <div className="flex items-center gap-3 mb-5">
        <h1 className="text-lg font-medium">Veículos</h1>
        <div className="flex-1" />
        <Button variant="primary" onClick={() => setModal('novo')}>+ Novo veículo</Button>
      </div>
      <ApiError error={error} />
      <Table>
        <thead>
          <tr><Th>Placa</Th><Th>Modelo</Th><Th>Marca</Th><Th>Ano</Th><Th>Cor</Th><Th>Cliente</Th><Th></Th></tr>
        </thead>
        <tbody>
          {loading && <EmptyRow cols={7} message="Carregando…" />}
          {!loading && data.content.length === 0 && <EmptyRow cols={7} />}
          {!loading && data.content.map((c) => (
            <tr key={c.id} className="hover:bg-gray-50">
              <Td className="font-mono font-medium">{c.placa}</Td>
              <Td>{c.modelo}</Td><Td>{c.marca}</Td>
              <Td>{c.ano}</Td><Td>{c.cor}</Td>
              <Td>{c.clienteNome ?? '—'}</Td>
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
        <CarroForm
          carro={modal === 'novo' ? null : modal}
          onClose={() => setModal(null)}
          onSaved={() => { setModal(null); load() }}
        />
      )}
    </div>
  )
}
