import api from './api'

export const pagamentoService = {
  registrar: (dados) => api.post('/pagamentos', dados),
  buscarPorOrdem: (ordemId) => api.get(`/pagamentos/ordem/${ordemId}`),
}
