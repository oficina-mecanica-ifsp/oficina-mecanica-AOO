import { useState } from 'react'
import { clienteService } from '../../services/clienteService'
import { carroService } from '../../services/carroService'
import { ordemService } from '../../services/ordemService'
import { Modal } from '../../components/ui/Modal'
import { Input } from '../../components/ui/Input'
import { Button } from '../../components/ui/Button'
import { ApiError } from '../../components/ui/Alert'

export default function OrdemForm({ onClose, onSaved }) {
  const [doc, setDoc] = useState('')
  const [cliente, setCliente] = useState(null)
  const [carros, setCarros] = useState([])
  const [carroId, setCarroId] = useState('')
  const [apiError, setApiError] = useState(null)
  const [loading, setLoading] = useState(false)

  const buscarCliente = async () => {
    setApiError(null); setCliente(null); setCarros([])
    try {
      const { data } = await clienteService.buscarPorDoc(doc.replace(/\D/g, ''))
      setCliente(data)
      const { data: cvs } = await carroService.listarPorCliente(data.id)
      setCarros(cvs.content ?? cvs)
    } catch (err) { setApiError(err) }
  }

  const handleSubmit = async () => {
    if (!cliente) return
    setLoading(true); setApiError(null)
    try {
      await ordemService.criar({ clienteId: cliente.id, carroId: carroId || null })
      onSaved()
    } catch (err) { setApiError(err) }
    finally { setLoading(false) }
  }

  return (
    <Modal title="Nova Ordem de Serviço" onClose={onClose}>
      <ApiError error={apiError} />
      <div className="flex flex-col gap-4">
        <div className="flex gap-2 items-end">
          <div className="flex-1">
            <Input
              label="CPF ou CNPJ do cliente (só números)"
              value={doc}
              onChange={(e) => setDoc(e.target.value)}
              maxLength={14}
            />
          </div>
          <Button onClick={buscarCliente}>Buscar</Button>
        </div>

        {cliente && (
          <div className="bg-green-50 border border-green-200 rounded-lg px-4 py-2.5 text-sm text-green-800">
            ✓ {cliente.nome}
          </div>
        )}

        {carros.length > 0 && (
          <div className="flex flex-col gap-1">
            <label className="text-xs text-gray-500 font-mono">Veículo (opcional)</label>
            <select
              value={carroId}
              onChange={(e) => setCarroId(e.target.value)}
              className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-700"
            >
              <option value="">Sem veículo</option>
              {carros.map((c) => (
                <option key={c.id} value={c.id}>{c.placa} — {c.modelo} {c.marca}</option>
              ))}
            </select>
          </div>
        )}

        <div className="flex gap-2 justify-end mt-2">
          <Button type="button" onClick={onClose}>Cancelar</Button>
          <Button variant="primary" disabled={!cliente} loading={loading} onClick={handleSubmit}>
            Abrir OS
          </Button>
        </div>
      </div>
    </Modal>
  )
}
