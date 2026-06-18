import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'

export function Topbar() {
  const { email, role, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <header className="h-13 bg-green-900 flex items-center px-5 gap-3 flex-shrink-0">
      <span className="font-mono text-sm text-white/90 tracking-tight">
        oficina<span className="text-white/40 mx-1">/</span>ifsp
      </span>
      <div className="flex-1" />
      <span className="text-white/60 text-xs">{email}</span>
      <span className="bg-white/10 text-white text-[10px] font-mono px-2 py-0.5 rounded">
        {role}
      </span>
      <button
        onClick={handleLogout}
        className="text-white/70 hover:text-white text-xs border border-white/20 px-3 py-1 rounded transition-colors"
      >
        Sair
      </button>
    </header>
  )
}
