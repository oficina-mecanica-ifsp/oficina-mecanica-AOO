export function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null
  return (
    <div className="flex items-center justify-end gap-1 mt-3">
      <button
        className="px-2.5 py-1 text-xs border border-gray-200 rounded bg-white hover:bg-gray-50 disabled:opacity-40"
        disabled={page === 0}
        onClick={() => onPageChange(page - 1)}
      >
        ‹
      </button>
      {Array.from({ length: totalPages }, (_, i) => (
        <button
          key={i}
          onClick={() => onPageChange(i)}
          className={`px-2.5 py-1 text-xs border rounded transition-colors ${
            i === page
              ? 'bg-brand-900 border-brand-900 text-white'
              : 'bg-white border-gray-200 text-gray-700 hover:bg-gray-50'
          }`}
        >
          {i + 1}
        </button>
      ))}
      <button
        className="px-2.5 py-1 text-xs border border-gray-200 rounded bg-white hover:bg-gray-50 disabled:opacity-40"
        disabled={page === totalPages - 1}
        onClick={() => onPageChange(page + 1)}
      >
        ›
      </button>
    </div>
  )
}
