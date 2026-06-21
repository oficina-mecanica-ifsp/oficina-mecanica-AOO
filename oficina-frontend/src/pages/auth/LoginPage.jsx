import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { authService } from '../../services/authService'
import { useAuth } from '../../hooks/useAuth'
import { ApiError } from '../../components/ui/Alert'
import logo from '../../assets/logo.jpeg'

const schema = z.object({
  email: z.string().email('Email inválido'),
  senha: z.string().min(1, 'Senha obrigatória'),
})

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [apiError, setApiError] = useState(null)

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(schema),
  })

  const onSubmit = async ({ email, senha }) => {
    setApiError(null)
    try {
      const { data } = await authService.login(email, senha)
      login({ token: data.token, email: data.email, role: data.role })
      navigate('/ordens')
    } catch (err) {
      setApiError(err)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="bg-white border border-gray-200 rounded-xl p-10 w-full max-w-sm shadow-sm">
        <div className="mb-7 flex flex-col items-center text-center">
          <img src={logo} alt="Mecânica PRO" className="h-16 w-16 rounded-lg object-cover mb-3" />
          <h1 className="font-mono text-xl text-brand-900 font-medium">
            Mecânica<span className="text-brand-700 font-bold">PRO</span>
          </h1>
          <p className="text-xs text-gray-400 mt-1">Sistema de Gestão de Oficina Mecânica</p>
        </div>

        <ApiError error={apiError} />

        <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
          <div className="flex flex-col gap-1">
            <label className="text-xs text-gray-500 font-mono">Email</label>
            <input
              type="email"
              {...register('email')}
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-700"
              placeholder="seu@email.com"
              autoFocus
            />
            {errors.email && <p className="text-xs text-red-500">{errors.email.message}</p>}
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-xs text-gray-500 font-mono">Senha</label>
            <input
              type="password"
              {...register('senha')}
              className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-700"
              placeholder="••••••••"
            />
            {errors.senha && <p className="text-xs text-red-500">{errors.senha.message}</p>}
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="bg-brand-900 text-white rounded px-4 py-2 text-sm font-medium hover:bg-brand-800 transition-colors disabled:opacity-60 mt-1"
          >
            {isSubmitting ? 'Entrando…' : 'Entrar'}
          </button>
        </form>
      </div>
    </div>
  )
}
