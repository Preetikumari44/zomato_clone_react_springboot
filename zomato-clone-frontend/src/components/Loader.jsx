export default function Loader({ label = 'Loading…' }) {
  return (
    <div className="flex items-center justify-center gap-2 py-12 text-ink/50">
      <span className="h-4 w-4 animate-spin rounded-full border-2 border-ink/20 border-t-marigold" />
      <span className="text-sm">{label}</span>
    </div>
  )
}
