import api from './api'

export const produtoService = {
  listar: (params) => api.get('/produtos', { params }),
  buscarPorId: (id) => api.get(`/produtos/${id}`),
  estoqueCritico: () => api.get('/produtos/estoque-critico'),
  criar: (dados) => api.post('/produtos', dados),
  atualizar: (id, dados) => api.put(`/produtos/${id}`, dados),
  excluir: (id) => api.delete(`/produtos/${id}`),
  entradaEstoque: (id, quantidade) => api.post(`/produtos/${id}/entrada`, { quantidade }),
}
