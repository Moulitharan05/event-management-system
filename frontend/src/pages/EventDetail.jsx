import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext.jsx'

export default function EventDetail() {
  const { id } = useParams()
  const [event, setEvent] = useState(null)
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState(null)
  const [registering, setRegistering] = useState(false)
  const { user } = useAuth()
  const navigate = useNavigate()

  const loadEvent = async () => {
    setLoading(true)
    try {
      const { data } = await api.get(`/events/${id}`)
      setEvent(data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadEvent()
  }, [id])

  const handleRegister = async () => {
    if (!user) {
      navigate('/login')
      return
    }
    setRegistering(true)
    setMessage(null)
    try {
      await api.post(`/registrations/events/${id}`)
      setMessage({ type: 'success', text: "You're registered! Check your email for confirmation." })
      loadEvent()
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Could not complete registration' })
    } finally {
      setRegistering(false)
    }
  }

  if (loading) return <div className="container page">Loading…</div>
  if (!event) return <div className="container page">Event not found.</div>

  const spotsLeft = event.capacity != null ? event.capacity - event.registeredCount : null
  const eventDate = new Date(event.eventDate)

  return (
    <div className="container page" style={{ maxWidth: 720 }}>
      <div className="card" style={{ padding: 32 }}>
        {event.category && <span className="ticket-tag">{event.category}</span>}
        <h1 style={{ margin: '12px 0' }}>{event.title}</h1>
        <p style={{ color: 'var(--ink-soft)' }}>
          📅 {eventDate.toLocaleString([], { dateStyle: 'full', timeStyle: 'short' })}<br />
          📍 {event.venue}
        </p>
        {event.description && <p style={{ lineHeight: 1.6, marginTop: 16 }}>{event.description}</p>}

        {event.speakers && event.speakers.length > 0 && (
          <div style={{ marginTop: 20 }}>
            <h3 style={{ fontSize: 16, marginBottom: 8 }}>Speakers</h3>
            {event.speakers.map((s) => (
              <span className="speaker-chip" key={s.id}>🎤 {s.name}{s.company ? ` · ${s.company}` : ''}</span>
            ))}
          </div>
        )}

        <div style={{ marginTop: 24, display: 'flex', alignItems: 'center', gap: 16 }}>
          <button className="btn btn-accent" onClick={handleRegister} disabled={registering || spotsLeft === 0}>
            {spotsLeft === 0 ? 'Fully booked' : registering ? 'Registering…' : 'Register for this event'}
          </button>
          {spotsLeft !== null && <span className="capacity-note">{spotsLeft} spot{spotsLeft === 1 ? '' : 's'} left</span>}
        </div>

        {message && (
          <div className={`alert alert-${message.type === 'success' ? 'success' : 'error'}`} style={{ marginTop: 16 }}>
            {message.text}
          </div>
        )}
      </div>
    </div>
  )
}
