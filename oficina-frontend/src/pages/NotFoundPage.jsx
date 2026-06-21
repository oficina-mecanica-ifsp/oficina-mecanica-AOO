import { Link } from 'react-router-dom'

export default function NotFoundPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <p className="font-mono text-6xl font-medium text-gray-200 mb-4">404</p>
        <h1 className="text-xl font-medium text-gray-700 mb-2">Página não encontrada</h1>
        <p className="text-sm text-gray-400 mb-6">O endereço acessado não existe.</p>
        <Link
          to="/ordens"
          className="inline-flex items-center gap-2 bg-brand-900 text-white px-5 py-2 rounded text-sm hover:bg-brand-800 transition-colors"
        >
          ← Voltar ao início
        </Link>
      </div>
    </div>
  )
}
