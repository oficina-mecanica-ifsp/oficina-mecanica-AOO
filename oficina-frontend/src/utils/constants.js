export const ORDER_STATUS_TRANSITIONS = {
  RECEBIDO: ['EM_DIAGNOSTICO', 'EM_ATRASO'],
  EM_DIAGNOSTICO: ['AGUARDANDO_APROVACAO', 'EM_ATRASO'],
  AGUARDANDO_APROVACAO: ['EM_EXECUCAO', 'EM_ATRASO'],
  EM_EXECUCAO: ['FINALIZADO', 'EM_ATRASO'],
  FINALIZADO: ['ENTREGUE'],
  ENTREGUE: [],
  EM_ATRASO: ['EM_EXECUCAO'],
}

export const STATUS_LABELS = {
  RECEBIDO: 'Recebido',
  EM_DIAGNOSTICO: 'Em Diagnóstico',
  AGUARDANDO_APROVACAO: 'Ag. Aprovação',
  EM_EXECUCAO: 'Em Execução',
  FINALIZADO: 'Finalizado',
  ENTREGUE: 'Entregue',
  EM_ATRASO: 'Em Atraso',
}

export const STATUS_COLORS = {
  RECEBIDO: 'bg-blue-100 text-blue-700',
  EM_DIAGNOSTICO: 'bg-yellow-100 text-yellow-700',
  AGUARDANDO_APROVACAO: 'bg-purple-100 text-purple-700',
  EM_EXECUCAO: 'bg-green-100 text-green-700',
  FINALIZADO: 'bg-emerald-100 text-emerald-700',
  ENTREGUE: 'bg-gray-100 text-gray-600',
  EM_ATRASO: 'bg-red-100 text-red-700',
}

export const STATUS_ORDER = [
  'RECEBIDO',
  'EM_DIAGNOSTICO',
  'AGUARDANDO_APROVACAO',
  'EM_EXECUCAO',
  'FINALIZADO',
  'ENTREGUE',
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
