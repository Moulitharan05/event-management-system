import React, { useState } from 'react'

export default function AdminSpeakerForm({ onSubmit }) {
  const [form, setForm] = useState({ name: '', bio: '', company: '' })

  const handleSubmit = (e) => {
    e.preventDefault()
    onSubmit(form)
    setForm({ name: '', bio: '', company: '' })
  }

  return (
    <form onSubmit={handleSubmit} className="grid-2" style={{ alignItems: 'end', marginBottom: 16 }}>
      <div className="form-group" style={{ marginBottom: 0 }}>
        <label>Speaker name</label>
        <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
      </div>
      <div className="form-group" style={{ marginBottom: 0 }}>
        <label>Company</label>
        <input value={form.company} onChange={(e) => setForm({ ...form, company: e.target.value })} />
      </div>
      <div className="form-group" style={{ gridColumn: '1 / -1' }}>
        <label>Short bio</label>
        <input value={form.bio} onChange={(e) => setForm({ ...form, bio: e.target.value })} />
      </div>
      <button className="btn btn-primary" type="submit" style={{ gridColumn: '1 / -1', justifySelf: 'start' }}>
        Add speaker
      </button>
    </form>
  )
}
