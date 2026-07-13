import React, { useEffect, useState } from 'react'
import api from '../api/axios'
import AdminEventForm from './AdminEventForm.jsx'
import AdminSpeakerForm from './AdminSpeakerForm.jsx'

export default function AdminDashboard() {
  const [events, setEvents] = useState([])
  const [speakers, setSpeakers] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [editingEvent, setEditingEvent] = useState(null)
  const [attendeesFor, setAttendeesFor] = useState(null)
  const [attendees, setAttendees] = useState([])
  const [tab, setTab] = useState('events')
  const [error, setError] = useState('')

  const loadEvents = async () => {
    const { data } = await api.get('/events')
    setEvents(data)
  }

  const loadSpeakers = async () => {
    const { data } = await api.get('/speakers')
    setSpeakers(data)
  }

  useEffect(() => {
    loadEvents()
    loadSpeakers()
  }, [])

  const handleCreateOrUpdate = async (form) => {
    setError('')
    try {
      if (editingEvent) {
        await api.put(`/admin/events/${editingEvent.id}`, form)
      } else {
        await api.post('/admin/events', form)
      }
      setShowForm(false)
      setEditingEvent(null)
      loadEvents()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not save event')
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this event? This cannot be undone.')) return
    try {
      await api.delete(`/admin/events/${id}`)
      loadEvents()
    } catch (err) {
      alert(err.response?.data?.message || 'Could not delete event')
    }
  }

  const handleAddSpeaker = async (form) => {
    try {
      await api.post('/admin/speakers', form)
      loadSpeakers()
    } catch (err) {
      alert(err.response?.data?.message || 'Could not add speaker')
    }
  }

  const handleDeleteSpeaker = async (id) => {
    if (!window.confirm('Remove this speaker?')) return
    try {
      await api.delete(`/admin/speakers/${id}`)
      loadSpeakers()
    } catch (err) {
      alert(err.response?.data?.message || 'Could not remove speaker')
    }
  }

  const viewAttendees = async (eventId) => {
    setAttendeesFor(eventId)
    const { data } = await api.get(`/admin/events/${eventId}/attendees`)
    setAttendees(data)
  }

  const toggleAttendance = async (registrationId, attended) => {
    await api.patch(`/admin/registrations/${registrationId}/attendance`, null, { params: { attended: !attended } })
    viewAttendees(attendeesFor)
  }

  return (
    <div className="container page">
      <h2 className="section-title">Admin dashboard</h2>
      {error && <div className="alert alert-error">{error}</div>}

      <div style={{ display: 'flex', gap: 10, marginBottom: 24 }}>
        <button className={`btn ${tab === 'events' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setTab('events')}>Events</button>
        <button className={`btn ${tab === 'speakers' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setTab('speakers')}>Speakers</button>
      </div>

      {tab === 'events' && (
        <>
          {!showForm && (
            <button className="btn btn-accent" style={{ marginBottom: 20 }} onClick={() => { setShowForm(true); setEditingEvent(null) }}>
              + New event
            </button>
          )}
          {showForm && (
            <AdminEventForm
              initialData={editingEvent}
              speakers={speakers}
              onSubmit={handleCreateOrUpdate}
              onCancel={() => { setShowForm(false); setEditingEvent(null) }}
            />
          )}

          <div className="card" style={{ padding: 8 }}>
            <table>
              <thead>
                <tr>
                  <th>Title</th><th>Date</th><th>Venue</th><th>Registered</th><th></th>
                </tr>
              </thead>
              <tbody>
                {events.map((ev) => (
                  <React.Fragment key={ev.id}>
                    <tr>
                      <td>{ev.title}</td>
                      <td>{new Date(ev.eventDate).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })}</td>
                      <td>{ev.venue}</td>
                      <td>{ev.registeredCount}/{ev.capacity}</td>
                      <td style={{ display: 'flex', gap: 8 }}>
                        <button className="btn btn-outline" onClick={() => { setEditingEvent(ev); setShowForm(true) }}>Edit</button>
                        <button className="btn btn-outline" onClick={() => viewAttendees(ev.id)}>Attendance</button>
                        <button className="btn btn-danger" onClick={() => handleDelete(ev.id)}>Delete</button>
                      </td>
                    </tr>
                    {attendeesFor === ev.id && (
                      <tr>
                        <td colSpan={5}>
                          {attendees.length === 0 ? (
                            <p style={{ color: 'var(--ink-soft)', fontSize: 14 }}>No one has registered yet.</p>
                          ) : (
                            <table>
                              <thead><tr><th>Name</th><th>Email</th><th>Registered at</th><th>Attended</th></tr></thead>
                              <tbody>
                                {attendees.map((a) => (
                                  <tr key={a.id}>
                                    <td>{a.userFullName}</td>
                                    <td>{a.userEmail}</td>
                                    <td>{new Date(a.registeredAt).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })}</td>
                                    <td>
                                      <button className="btn btn-outline" onClick={() => toggleAttendance(a.id, a.attended)}>
                                        {a.attended ? 'Mark absent' : 'Mark attended'}
                                      </button>
                                    </td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          )}
                        </td>
                      </tr>
                    )}
                  </React.Fragment>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      {tab === 'speakers' && (
        <div className="card" style={{ padding: 24 }}>
          <AdminSpeakerForm onSubmit={handleAddSpeaker} />
          <table>
            <thead><tr><th>Name</th><th>Company</th><th>Bio</th><th></th></tr></thead>
            <tbody>
              {speakers.map((s) => (
                <tr key={s.id}>
                  <td>{s.name}</td>
                  <td>{s.company}</td>
                  <td>{s.bio}</td>
                  <td><button className="btn btn-danger" onClick={() => handleDeleteSpeaker(s.id)}>Remove</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
