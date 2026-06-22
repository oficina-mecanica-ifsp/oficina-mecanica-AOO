export const ORDER_STATUS_TRANSITIONS = {
  ABERTO: ['EM_ANDAMENTO', 'EM_ATRASO'],
  EM_ANDAMENTO: ['FINALIZADO', 'EM_ATRASO'],
  EM_ATRASO: ['EM_ANDAMENTO'],
  FINALIZADO: [],
}

export const STATUS_LABELS = {
  ABERTO: 'Aberto',
  EM_ANDAMENTO: 'Em Andamento',
  EM_ATRASO: 'Em Atraso',
  FINALIZADO: 'Finalizado',
}

export const STATUS_COLORS = {
  ABERTO: 'bg-blue-100 text-blue-700',
  EM_ANDAMENTO: 'bg-yellow-100 text-yellow-700',
  EM_ATRASO: 'bg-red-100 text-red-700',
  FINALIZADO: 'bg-emerald-100 text-emerald-700',
}

export const STATUS_ORDER = [
  'ABERTO',
  'EM_ANDAMENTO',
  'EM_ATRASO',
  'FINALIZADO',
]

export const FORMAS_PAGAMENTO = [
  'DINHEIRO',
  'PIX',
  'CARTAO_CREDITO',
  'CARTAO_DEBITO',
  'TRANSFERENCIA',
]

export const TIPO_PRODUTO = ['PECA', 'PRODUTO']

export const TIPO_USUARIO = ['ADMIN', 'ATENDENTE', 'MECANICO']