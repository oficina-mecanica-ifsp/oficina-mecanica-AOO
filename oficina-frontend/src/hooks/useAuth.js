import useAuthStore from '../store/authStore'

export function useAuth() {
  const { token, email, role, login, logout } = useAuthStore()
  const isAuthenticated = !!token
  const can = (roles) => roles.includes(role)
  return { token, email, role, login, logout, isAuthenticated, can }
}
