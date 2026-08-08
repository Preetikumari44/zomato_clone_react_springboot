const STYLES = {
  PENDING: 'bg-marigold/15 text-marigold-dark',
  APPROVED: 'bg-basil/15 text-basil',
  REJECTED: 'bg-chili/15 text-chili',
  SUSPENDED: 'bg-ink/10 text-ink/60',
  PLACED: 'bg-marigold/15 text-marigold-dark',
  ACCEPTED: 'bg-basil/15 text-basil',
  PREPARING: 'bg-marigold/15 text-marigold-dark',
  READY_FOR_PICKUP: 'bg-basil/15 text-basil',
  PICKED_UP: 'bg-basil/15 text-basil',
  DELIVERED: 'bg-basil/20 text-basil',
  CANCELLED: 'bg-ink/10 text-ink/60',
  UNASSIGNED: 'bg-ink/10 text-ink/60',
  ASSIGNED: 'bg-marigold/15 text-marigold-dark',
}

export default function StatusBadge({ status }) {
  const style = STYLES[status] || 'bg-ink/10 text-ink/60'
  return (
    <span className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-semibold ${style}`}>
      {status?.replaceAll('_', ' ')}
    </span>
  )
}
