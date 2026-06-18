import { Navigate, Outlet } from 'react-router-dom'
import useAuthStore from '../../store/authStore'

export function RoleGuard({ roles }) {
  const role = useAuthStore((s) => s.role)
  if (!roles.includes(role)) return <Navigate to="/ordens" replace />
  return <Outlet />
}
