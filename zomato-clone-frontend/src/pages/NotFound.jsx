import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-3 text-center">
      <h1 className="text-3xl font-semibold">Page not found</h1>
      <p className="text-ink/60">There's nothing here.</p>
      <Link to="/" className="btn-primary">Back home</Link>
    </div>
  )
}
