import api from './api'

export const servicoService = {
  listar: (params) => api.get('/servicos', { params }),
  buscarPorId: (id) => api.get(`/servicos/${id}`),
  criar: (dados) => api.post('/servicos', dados),
  atualizar: (id, dados) => api.put(`/servicos/${id}`, dados),
  excluir: (id) => api.delete(`/servicos/${id}`),
}
