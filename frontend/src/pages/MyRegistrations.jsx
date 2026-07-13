import React, { useEffect, useState } from 'react'
import api from '../api/axios'

export default function MyRegistrations() {
  const [registrations, setRegistrations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = async () => {
    setLoading(true)
    try {
      const { data } = await api.get('/registrations/me')
      setRegistrations(data)
    } catch (err) {
      setError('Could not load your registrations')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleCancel = async (eventId) => {
    if (!window.confirm('Cancel this registration?')) return
    try {
      await api.delete(`/registrations/events/${eventId}`)
      load()
    } catch (err) {
      alert(err.response?.data?.message || 'Could not cancel registration')
    }
  }

  return (
    <div className="container page">
      <h2 className="section-title">My registrations</h2>
      {error && <div className="alert alert-error">{error}</div>}
      {loading ? (
        <p>Loading…</p>
      ) : registrations.length === 0 ? (
        <div className="empty-state card">You haven't registered for any events yet.</div>
      ) : (
        <div className="card" style={{ padding: 8 }}>
          <table>
            <thead>
              <tr>
                <th>Event</th>
                <th>Date</th>
                <th>Venue</th>
                <th>Attended</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {registrations.map((r) => (
                <tr key={r.id}>
                  <td>{r.eventTitle}</td>
                  <td>{new Date(r.eventDate).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })}</td>
                  <td>{r.venue}</td>
                  <td><span className={`badge ${r.attended ? 'badge-yes' : 'badge-no'}`}>{r.attended ? 'Yes' : 'Not yet'}</span></td>
                  <td><button className="btn btn-outline" onClick={() => handleCancel(r.eventId)}>Cancel</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
