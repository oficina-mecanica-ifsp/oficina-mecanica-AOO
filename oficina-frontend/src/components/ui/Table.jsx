export function Table({ children }) {
  return (
    <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
      <table className="w-full text-sm">{children}</table>
    </div>
  )
}

export function Th({ children, className = '' }) {
  return (
    <th className={`px-4 py-2.5 text-left text-xs font-mono text-gray-400 tracking-wider bg-gray-50 border-b border-gray-100 ${className}`}>
      {children}
    </th>
  )
}

export function Td({ children, className = '' }) {
  return (
    <td className={`px-4 py-2.5 text-gray-700 border-b border-gray-50 ${className}`}>
      {children}
    </td>
  )
}

export function EmptyRow({ cols, message = 'Nenhum registro encontrado.' }) {
  return (
    <tr>
      <td colSpan={cols} className="px-4 py-8 text-center text-gray-400 text-sm">
        {message}
      </td>
    </tr>
  )
}
