export const formatBRL = (value) =>
  new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value ?? 0)

export const formatDate = (value) =>
  value ? new Date(value).toLocaleString('pt-BR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  }) : '—'

export const formatCPFCNPJ = (value = '') => {
  const v = value.replace(/\D/g, '')
  if (v.length === 11)
    return v.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
  if (v.length === 14)
    return v.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
  return value
}

export const parseDuration = (iso = '') => {
  const m = iso.match(/PT(?:(\d+)H)?(?:(\d+)M)?/)
  if (!m) return iso
  const h = m[1] ? `${m[1]}h` : ''
  const min = m[2] ? `${m[2]}min` : ''
  return [h, min].filter(Boolean).join(' ') || '0min'
}

export const buildDuration = (hours, minutes) =>
  `PT${parseInt(hours) || 0}H${parseInt(minutes) > 0 ? `${parseInt(minutes)}M` : ''}`
