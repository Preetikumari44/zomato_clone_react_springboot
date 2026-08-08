import { Link } from 'react-router-dom'

export default function Unauthorized() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-3 text-center">
      <h1 className="text-3xl font-semibold">Not available in this role</h1>
      <p className="text-ink/60">Switch roles from the navbar if you need to get here.</p>
      <Link to="/" className="btn-primary">Back home</Link>
    </div>
  )
}
