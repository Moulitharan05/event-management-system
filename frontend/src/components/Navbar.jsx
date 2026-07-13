import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <nav className="navbar">
      <div className="container">
        <Link to="/" className="brand">Event<span className="dot">ra</span></Link>
        <div className="nav-links">
          <Link to="/">Browse Events</Link>
          {user && !isAdmin && <Link to="/my-registrations">My Registrations</Link>}
          {isAdmin && <Link to="/admin">Admin Dashboard</Link>}
          {user ? (
            <button onClick={handleLogout}>Sign out</button>
          ) : (
            <>
              <Link to="/login">Sign in</Link>
              <Link to="/register" className="nav-cta">Sign up</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}
