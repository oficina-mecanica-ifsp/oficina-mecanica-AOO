import api from './api'

export const carroService = {
  listar: (params) => api.get('/carros', { params }),
  listarPorCliente: (clienteId) => api.get(`/clientes/${clienteId}/carros`),
  buscarPorId: (id) => api.get(`/carros/${id}`),
  criar: (dados) => api.post('/carros', dados),
  atualizar: (id, dados) => api.put(`/carros/${id}`, dados),
  excluir: (id) => api.delete(`/carros/${id}`),
}
