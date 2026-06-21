import { NavLink } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'

const NAV = [
  { to: '/ordens', label: 'Ordens de Serviço', icon: '📋', roles: ['ADMIN', 'ATENDENTE', 'MECANICO'], section: 'Operação' },
  { to: '/clientes', label: 'Clientes', icon: '👥', roles: ['ADMIN', 'ATENDENTE', 'MECANICO'], section: 'Cadastros' },
  { to: '/carros', label: 'Veículos', icon: '🚗', roles: ['ADMIN', 'ATENDENTE', 'MECANICO'] },
  { to: '/produtos', label: 'Produtos', icon: '📦', roles: ['ADMIN', 'ATENDENTE', 'MECANICO'] },
  { to: '/servicos', label: 'Serviços', icon: '🔧', roles: ['ADMIN', 'ATENDENTE', 'MECANICO'] },
  { to: '/estoque-critico', label: 'Estoque Crítico', icon: '⚠️', roles: ['ADMIN', 'ATENDENTE'], section: 'Relatórios' },
  { to: '/usuarios', label: 'Usuários', icon: '🔐', roles: ['ADMIN'], section: 'Administração' },
]

export function Sidebar() {
  const { can } = useAuth()
  let lastSection = null

  return (
    <aside className="w-56 bg-white border-r border-gray-100 flex-shrink-0 flex flex-col overflow-y-auto">
      {NAV.filter((item) => can(item.roles)).map((item) => {
        const showSection = item.section && item.section !== lastSection
        if (item.section) lastSection = item.section
        return (
          <div key={item.to}>
            {showSection && (
              <p className="px-4 pt-4 pb-1 text-[10px] font-mono text-gray-400 uppercase tracking-widest">
                {item.section}
              </p>
            )}
            <NavLink
              to={item.to}
              className={({ isActive }) =>
                `flex items-center gap-2.5 px-4 py-2 text-sm transition-colors border-l-2 ${
                  isActive
                    ? 'border-brand-800 bg-brand-50 text-brand-900 font-medium'
                    : 'border-transparent text-gray-500 hover:bg-gray-50 hover:text-gray-800'
                }`
              }
            >
              <span>{item.icon}</span>
              {item.label}
            </NavLink>
          </div>
        )
      })}
    </aside>
  )
}
