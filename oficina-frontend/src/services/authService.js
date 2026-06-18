import api from './api'

export const authService = {
  login: (email, senha) => api.post('/auth/login', { email, senha }),
  cadastrar: (dados) => api.post('/auth/cadastrar', dados),
}
