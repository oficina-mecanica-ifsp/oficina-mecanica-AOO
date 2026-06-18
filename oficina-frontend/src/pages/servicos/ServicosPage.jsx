import { useState, useEffect, useCallback } from 'react'
import { servicoService } from '../../services/servicoService'
import { Table, Th, Td, EmptyRow } from '../../components/ui/Table'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'
import { formatBRL, parseDuration } from '../../utils/formatters'
import ServicoForm from './ServicoForm'

export default function ServicosPage() {
  const [data, setData] = useState({ content: [] })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [modal, setModal] = useState(null)

  const load = useCallback(async () => {
    setLoading(true); setError(null)
    try { const { data: res } = await servicoService.listar({ size: 100 }); setData(res) }
    catch (err) { setError(err) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { load() }, [load])

  const handleDelete = async (id) => {
    if (!confirm('Excluir serviço?')) return
    try { await servicoService.excluir(id); load() }
    catch (err) { setError(err) }
  }

  return (
    <div>
      <div className="flex items-center gap-3 mb-5">
        <h1 className="text-lg font-medium">Serviços</h1>
        <div className="flex-1" />
        <Button variant="primary" onClick={() => setModal('novo')}>+ Novo serviço</Button>
      </div>
      <ApiError error={error} />
      <Table>
        <thead><tr><Th>Nome</Th><Th>Preço</Th><Th>Duração</Th><Th></Th></tr></thead>
        <tbody>
          {loading && <EmptyRow cols={4} message="Carregando…" />}
          {!loading && (data.content ?? []).map((s) => (
            <tr key={s.id} className="hover:bg-gray-50">
              <Td className="font-medium">{s.nome}</Td>
              <Td className="font-mono">{formatBRL(s.preco)}</Td>
              <Td className="font-mono text-xs">{parseDuration(s.duracao)}</Td>
              <Td>
                <div className="flex gap-2">
                  <Button size="xs" onClick={() => setModal(s)}>Editar</Button>
                  <Button size="xs" variant="danger" onClick={() => handleDelete(s.id)}>Excluir</Button>
                </div>
              </Td>
            </tr>
          ))}
        </tbody>
      </Table>
      {modal && (
        <ServicoForm
          servico={modal === 'novo' ? null : modal}
          onClose={() => setModal(null)}
          onSaved={() => { setModal(null); load() }}
        />
      )}
    </div>
  )
}
