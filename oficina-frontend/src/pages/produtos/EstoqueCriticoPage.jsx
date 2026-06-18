import { useState, useEffect } from 'react'
import { produtoService } from '../../services/produtoService'
import { Table, Th, Td, EmptyRow } from '../../components/ui/Table'
import { Button } from '../../components/ui/Button'
import { Alert, ApiError } from '../../components/ui/Alert'
import { formatBRL } from '../../utils/formatters'
import EntradaEstoqueModal from './EntradaEstoqueModal'

export default function EstoqueCriticoPage() {
  const [produtos, setProdutos] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [entradaModal, setEntradaModal] = useState(null)

  const load = async () => {
    setLoading(true); setError(null)
    try { const { data } = await produtoService.estoqueCritico(); setProdutos(data) }
    catch (err) { setError(err) }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  return (
    <div>
      <div className="flex items-center gap-3 mb-5">
        <div>
          <h1 className="text-lg font-medium">Estoque Crítico</h1>
          <p className="text-xs text-red-500 mt-0.5">{produtos.length} produto(s) abaixo do mínimo</p>
        </div>
      </div>

      <ApiError error={error} />

      {!loading && produtos.length === 0 && (
        <Alert type="success">✓ Nenhum produto com estoque crítico no momento.</Alert>
      )}

      {produtos.length > 0 && (
        <>
          <Alert type="danger">⚠ Os produtos abaixo estão com estoque inferior à quantidade mínima.</Alert>
          <Table>
            <thead>
              <tr><Th>Produto</Th><Th>Tipo</Th><Th>Estoque atual</Th><Th>Qtd mínima</Th><Th>Déficit</Th><Th></Th></tr>
            </thead>
            <tbody>
              {loading && <EmptyRow cols={6} message="Carregando…" />}
              {!loading && produtos.map((p) => (
                <tr key={p.id} className="bg-red-50">
                  <Td className="font-medium text-red-700">⚠ {p.nome}</Td>
                  <Td><span className="text-xs font-mono bg-blue-100 text-blue-700 px-2 py-0.5 rounded">{p.tipo}</span></Td>
                  <Td className="font-mono font-medium text-red-600">{p.quantidade}</Td>
                  <Td className="font-mono">{p.qtdMinima}</Td>
                  <Td className="font-mono text-red-600">−{p.qtdMinima - p.quantidade}</Td>
                  <Td><Button size="xs" onClick={() => setEntradaModal(p)}>Entrada</Button></Td>
                </tr>
              ))}
            </tbody>
          </Table>
        </>
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
