import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AppLayout } from './components/layout/AppLayout'
import { ProtectedRoute } from './components/guards/ProtectedRoute'
import { RoleGuard } from './components/guards/RoleGuard'

import LoginPage from './pages/auth/LoginPage'
import NotFoundPage from './pages/NotFoundPage'

import OrdensPage from './pages/ordens/OrdensPage'
import OrdemDetalhePage from './pages/ordens/OrdemDetalhePage'

import ClientesPage from './pages/clientes/ClientesPage'
import CarrosPage from './pages/carros/CarrosPage'

import ProdutosPage from './pages/produtos/ProdutosPage'
import EstoqueCriticoPage from './pages/produtos/EstoqueCriticoPage'

import ServicosPage from './pages/servicos/ServicosPage'
import UsuariosPage from './pages/usuarios/UsuariosPage'

export default function App() {
  return (
    <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <Routes>
        {/* Rota pública */}
        <Route path="/login" element={<LoginPage />} />

        {/* Rotas autenticadas */}
        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            {/* Redirect raiz */}
            <Route index element={<Navigate to="/ordens" replace />} />

            {/* Ordens — todos autenticados */}
            <Route path="/ordens" element={<OrdensPage />} />
            <Route path="/ordens/:id" element={<OrdemDetalhePage />} />

            {/* Cadastros — todos autenticados */}
            <Route path="/clientes" element={<ClientesPage />} />
            <Route path="/carros" element={<CarrosPage />} />
            <Route path="/servicos" element={<ServicosPage />} />

            {/* Produtos — todos autenticados para visualizar */}
            <Route path="/produtos" element={<ProdutosPage />} />

            {/* Estoque crítico — ATENDENTE e ADMIN */}
            <Route element={<RoleGuard roles={['ADMIN', 'ATENDENTE']} />}>
              <Route path="/estoque-critico" element={<EstoqueCriticoPage />} />
            </Route>

            {/* Usuários — apenas ADMIN */}
            <Route element={<RoleGuard roles={['ADMIN']} />}>
              <Route path="/usuarios" element={<UsuariosPage />} />
            </Route>
          </Route>
        </Route>

        {/* 404 */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  )
}
