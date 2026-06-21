export function Alert({ type = 'danger', children }) {
  const styles = {
    danger: 'bg-red-50 border-red-200 text-red-700',
    success: 'bg-brand-50 border-brand-100 text-brand-700',
    warn: 'bg-yellow-50 border-yellow-200 text-yellow-700',
    info: 'bg-blue-50 border-blue-200 text-blue-700',
  }
  return (
    <div className={`border rounded-lg px-4 py-2.5 text-sm mb-3 ${styles[type]}`}>
      {children}
    </div>
  )
}

export function ApiError({ error }) {
  if (!error) return null
  const msg =
    error?.response?.data?.message ??
    error?.response?.data?.error ??
    error?.message ??
    'Erro inesperado'
  const status = error?.response?.status
  if (status === 403) return <Alert type="danger">Acesso negado. Você não tem permissão para esta ação.</Alert>
  if (status === 404) return <Alert type="danger">Recurso não encontrado.</Alert>
  if (status === 422) return <Alert type="warn">{msg}</Alert>
  return <Alert type="danger">{msg}</Alert>
}
