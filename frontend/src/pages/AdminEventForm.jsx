import React, { useState, useEffect } from 'react'

export default function AdminEventForm({ initialData, speakers, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    title: '', description: '', eventDate: '', venue: '', category: '', capacity: 100, speakerIds: []
  })

  useEffect(() => {
    if (initialData) {
      setForm({
        title: initialData.title || '',
        description: initialData.description || '',
        eventDate: initialData.eventDate ? initialData.eventDate.slice(0, 16) : '',
        venue: initialData.venue || '',
        category: initialData.category || '',
        capacity: initialData.capacity ?? 100,
        speakerIds: initialData.speakers ? initialData.speakers.map((s) => s.id) : []
      })
    }
  }, [initialData])

  const handleChange = (field, value) => setForm((f) => ({ ...f, [field]: value }))

  const toggleSpeaker = (id) => {
    setForm((f) => ({
      ...f,
      speakerIds: f.speakerIds.includes(id) ? f.speakerIds.filter((s) => s !== id) : [...f.speakerIds, id]
    }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    onSubmit({ ...form, capacity: Number(form.capacity), eventDate: new Date(form.eventDate).toISOString() })
  }

  return (
    <div className="card" style={{ padding: 28, marginBottom: 24 }}>
      <h3 style={{ marginBottom: 16 }}>{initialData ? 'Edit event' : 'Create a new event'}</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Title</label>
          <input value={form.title} onChange={(e) => handleChange('title', e.target.value)} required />
        </div>
        <div className="form-group">
          <label>Description</label>
          <textarea rows={3} value={form.description} onChange={(e) => handleChange('description', e.target.value)} />
        </div>
        <div className="grid-2">
          <div className="form-group">
            <label>Date &amp; time</label>
            <input type="datetime-local" value={form.eventDate} onChange={(e) => handleChange('eventDate', e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Venue</label>
            <input value={form.venue} onChange={(e) => handleChange('venue', e.target.value)} required />
          </div>
        </div>
        <div className="grid-2">
          <div className="form-group">
            <label>Category</label>
            <select value={form.category} onChange={(e) => handleChange('category', e.target.value)}>
              <option value="">None</option>
              <option value="Technology">Technology</option>
              <option value="Business">Business</option>
              <option value="Workshop">Workshop</option>
              <option value="Networking">Networking</option>
              <option value="Other">Other</option>
            </select>
          </div>
          <div className="form-group">
            <label>Capacity</label>
            <input type="number" min={1} value={form.capacity} onChange={(e) => handleChange('capacity', e.target.value)} required />
          </div>
        </div>
        <div className="form-group">
          <label>Speakers</label>
          <div>
            {speakers.length === 0 && <p style={{ fontSize: 13, color: 'var(--ink-soft)' }}>No speakers yet — add one below first.</p>}
            {speakers.map((s) => (
              <label key={s.id} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, marginRight: 14, fontSize: 14 }}>
                <input
                  type="checkbox"
                  checked={form.speakerIds.includes(s.id)}
                  onChange={() => toggleSpeaker(s.id)}
                />
                {s.name}
              </label>
            ))}
          </div>
        </div>
        <div style={{ display: 'flex', gap: 10, marginTop: 8 }}>
          <button className="btn btn-accent" type="submit">{initialData ? 'Save changes' : 'Create event'}</button>
          <button className="btn btn-outline" type="button" onClick={onCancel}>Cancel</button>
        </div>
      </form>
    </div>
  )
}
