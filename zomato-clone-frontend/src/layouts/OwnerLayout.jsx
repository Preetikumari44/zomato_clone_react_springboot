import { NavLink, Outlet } from 'react-router-dom'
import Navbar from '../components/Navbar'

const TABS = [
  { to: '/owner/restaurants', label: 'My Restaurants' },
  { to: '/owner/orders', label: 'Incoming Orders' },
]

export default function OwnerLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <div className="border-b border-line bg-white">
        <nav className="mx-auto flex max-w-6xl gap-6 px-4">
          {TABS.map((tab) => (
            <NavLink
              key={tab.to}
              to={tab.to}
              className={({ isActive }) =>
                `border-b-2 py-3 text-sm font-medium ${isActive ? 'border-marigold text-ink' : 'border-transparent text-ink/50 hover:text-ink'}`
              }
            >
              {tab.label}
            </NavLink>
          ))}
        </nav>
      </div>
      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}
