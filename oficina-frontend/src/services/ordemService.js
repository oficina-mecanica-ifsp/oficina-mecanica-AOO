import api from './api'

export const ordemService = {
  listar: (params) => api.get('/orders', { params }),
  buscarPorId: (id) => api.get(`/orders/${id}`),
  criar: (dados) => api.post('/orders', dados),
  atualizarCabecalho: (id, dados) => api.put(`/orders/${id}`, dados),
  excluir: (id) => api.delete(`/orders/${id}`),
  avancarStatus: (id, status, dataValidade) =>
    api.patch(`/orders/${id}/status`, { status, dataValidade }),
  adicionarProduto: (id, dados) => api.post(`/orders/${id}/produtos`, dados),
  removerProduto: (id, itemId) => api.delete(`/orders/${id}/produtos/${itemId}`),
  adicionarServico: (id, dados) => api.post(`/orders/${id}/servicos`, dados),
  removerServico: (id, itemId) => api.delete(`/orders/${id}/servicos/${itemId}`),
}
