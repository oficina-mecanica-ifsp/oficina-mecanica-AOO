import { forwardRef } from 'react'

export const Input = forwardRef(function Input(
  { label, error, className = '', ...props },
  ref
) {
  return (
    <div className="flex flex-col gap-1">
      {label && (
        <label className="text-xs text-gray-500 font-mono tracking-wide">{label}</label>
      )}
      <input
        ref={ref}
        className={`border rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-brand-700 focus:border-transparent transition
          ${error ? 'border-red-400 bg-red-50' : 'border-gray-300 bg-white'}
          ${className}`}
        {...props}
      />
      {error && <p className="text-xs text-red-600 font-mono">{error}</p>}
    </div>
  )
})

export const Select = forwardRef(function Select(
  { label, error, children, className = '', ...props },
  ref
) {
  return (
    <div className="flex flex-col gap-1">
      {label && (
        <label className="text-xs text-gray-500 font-mono tracking-wide">{label}</label>
      )}
      <select
        ref={ref}
        className={`border rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-brand-700 focus:border-transparent transition
          ${error ? 'border-red-400 bg-red-50' : 'border-gray-300 bg-white'}
          ${className}`}
        {...props}
      >
        {children}
      </select>
      {error && <p className="text-xs text-red-600 font-mono">{error}</p>}
    </div>
  )
})