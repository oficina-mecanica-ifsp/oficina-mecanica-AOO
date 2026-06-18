import api from './api'

export const clienteService = {
  listar: (params) => api.get('/clientes', { params }),
  buscarPorDoc: (doc) => api.get(`/clientes/doc/${doc}`),
  buscarPorId: (id) => api.get(`/clientes/${id}`),
  criar: (dados) => api.post('/clientes', dados),
  atualizar: (id, dados) => api.put(`/clientes/${id}`, dados),
  excluir: (id) => api.delete(`/clientes/${id}`),
}
