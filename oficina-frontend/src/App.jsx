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
        <Route path="/login" element={<LoginPage />} />

        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route index element={<Navigate to="/ordens" replace />} />
            <Route path="/ordens" element={<OrdensPage />} />
            <Route path="/ordens/:id" element={<OrdemDetalhePage />} />
            <Route path="/clientes" element={<ClientesPage />} />
            <Route path="/carros" element={<CarrosPage />} />
            <Route path="/servicos" element={<ServicosPage />} />
            <Route path="/produtos" element={<ProdutosPage />} />
            <Route element={<RoleGuard roles={['ADMIN', 'ATENDENTE']} />}>
              <Route path="/estoque-critico" element={<EstoqueCriticoPage />} />
            </Route>
            <Route element={<RoleGuard roles={['ADMIN']} />}>
              <Route path="/usuarios" element={<UsuariosPage />} />
            </Route>
          </Route>
        </Route>

        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  )
}