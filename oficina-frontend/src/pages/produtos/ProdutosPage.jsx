import { useState, useEffect, useCallback } from 'react'
import { produtoService } from '../../services/produtoService'
import { useAuth } from '../../hooks/useAuth'
import { Table, Th, Td, EmptyRow } from '../../components/ui/Table'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'
import { formatBRL } from '../../utils/formatters'
import ProdutoForm from './ProdutoForm'
import EntradaEstoqueModal from './EntradaEstoqueModal'

export default function ProdutosPage() {
  const { can } = useAuth()
  const [data, setData] = useState({ content: [], totalPages: 0 })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [modal, setModal] = useState(null)
  const [entradaModal, setEntradaModal] = useState(null)

  const load = useCallback(async () => {
    setLoading(true); setError(null)
    try { const { data: res } = await produtoService.listar({ size: 50 }); setData(res) }
    catch (err) { setError(err) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { load() }, [load])

  const handleDelete = async (id) => {
    if (!confirm('Excluir produto?')) return
    try { await produtoService.excluir(id); load() }
    catch (err) { setError(err) }
  }

  const canEdit = can(['ADMIN', 'ATENDENTE'])
  const canDelete = can(['ADMIN'])

  return (
    <div>
      <div className="flex items-center gap-3 mb-5">
        <h1 className="text-lg font-medium">Produtos</h1>
        <div className="flex-1" />
        {canEdit && <Button variant="primary" onClick={() => setModal('novo')}>+ Novo produto</Button>}
      </div>

      <ApiError error={error} />

      <Table>
        <thead>
          <tr><Th>Nome</Th><Th>Tipo</Th><Th>Preço</Th><Th>Estoque</Th><Th>Qtd mín.</Th><Th></Th></tr>
        </thead>
        <tbody>
          {loading && <EmptyRow cols={6} message="Carregando…" />}
          {!loading && (data.content ?? []).map((p) => {
            const critico = p.quantidade < p.qtdMinima
            return (
              <tr key={p.id} className={critico ? 'bg-red-50' : 'hover:bg-gray-50'}>
                <Td className={`font-medium ${critico ? 'text-red-700' : ''}`}>
                  {critico && '⚠ '}{p.nome}
                </Td>
                <Td>
                  <span className="text-xs font-mono bg-blue-100 text-blue-700 px-2 py-0.5 rounded">{p.tipo}</span>
                </Td>
                <Td className="font-mono">{formatBRL(p.preco)}</Td>
                <Td className={`font-mono font-medium ${critico ? 'text-red-600' : ''}`}>{p.quantidade}</Td>
                <Td className="font-mono">{p.qtdMinima}</Td>
                <Td>
                  <div className="flex gap-2">
                    {canEdit && (
                      <Button size="xs" onClick={() => setEntradaModal(p)}>Entrada</Button>
                    )}
                    {canEdit && <Button size="xs" onClick={() => setModal(p)}>Editar</Button>}
                    {canDelete && <Button size="xs" variant="danger" onClick={() => handleDelete(p.id)}>Excluir</Button>}
                  </div>
                </Td>
              </tr>
            )
          })}
        </tbody>
      </Table>

      {modal && (
        <ProdutoForm
          produto={modal === 'novo' ? null : modal}
          onClose={() => setModal(null)}
          onSaved={() => { setModal(null); load() }}
        />
      )}
      {entradaModal && (
        <EntradaEstoqueModal
          produto={entradaModal}
          onClose={() => setEntradaModal(null)}
          onSaved={() => { setEntradaModal(null); load() }}
        />
      )}
    </div>
  )
}
