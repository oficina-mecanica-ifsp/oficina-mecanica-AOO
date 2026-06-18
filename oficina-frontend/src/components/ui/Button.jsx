export function Button({
  children,
  variant = 'default',
  size = 'md',
  disabled = false,
  loading = false,
  className = '',
  ...props
}) {
  const base = 'inline-flex items-center gap-1.5 font-medium rounded border transition-colors focus:outline-none focus:ring-2 focus:ring-offset-1 disabled:opacity-50 disabled:cursor-not-allowed'

  const variants = {
    default: 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50 focus:ring-gray-300',
    primary: 'bg-green-900 border-green-900 text-white hover:bg-green-800 focus:ring-green-700',
    danger: 'bg-white border-red-400 text-red-600 hover:bg-red-50 focus:ring-red-300',
    ghost: 'bg-transparent border-transparent text-gray-600 hover:bg-gray-100 focus:ring-gray-200',
  }

  const sizes = {
    xs: 'px-2 py-0.5 text-xs',
    sm: 'px-3 py-1 text-sm',
    md: 'px-4 py-1.5 text-sm',
    lg: 'px-5 py-2 text-base',
  }

  return (
    <button
      disabled={disabled || loading}
      className={`${base} ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    >
      {loading && (
        <svg className="animate-spin h-3.5 w-3.5" viewBox="0 0 24 24" fill="none">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z" />
        </svg>
      )}
      {children}
    </button>
  )
}
