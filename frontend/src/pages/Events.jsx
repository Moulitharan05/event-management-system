import React, { useEffect, useState } from 'react'
import api from '../api/axios'
import EventTicketCard from '../components/EventTicketCard.jsx'

export default function Events() {
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [keyword, setKeyword] = useState('')
  const [category, setCategory] = useState('')

  const fetchEvents = async (params = {}) => {
    setLoading(true)
    try {
      const hasFilters = params.keyword || params.category
      const { data } = hasFilters
        ? await api.get('/events/search', { params })
        : await api.get('/events')
      setEvents(data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchEvents()
  }, [])

  const handleSearch = (e) => {
    e.preventDefault()
    fetchEvents({ keyword: keyword || undefined, category: category || undefined })
  }

  return (
    <div>
      <div className="hero">
        <div className="container">
          <h1>Find your next event</h1>
          <p>Browse talks, workshops, and meetups — register in a couple of taps and we'll handle the reminders.</p>
          <form className="search-bar" onSubmit={handleSearch}>
            <input
              placeholder="Search by title or description…"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />
            <select value={category} onChange={(e) => setCategory(e.target.value)}>
              <option value="">All categories</option>
              <option value="Technology">Technology</option>
              <option value="Business">Business</option>
              <option value="Workshop">Workshop</option>
              <option value="Networking">Networking</option>
              <option value="Other">Other</option>
            </select>
            <button className="btn btn-primary" type="submit">Search</button>
          </form>
        </div>
      </div>

      <div className="container page">
        <h2 className="section-title">Upcoming events</h2>
        {loading ? (
          <p>Loading events…</p>
        ) : events.length === 0 ? (
          <div className="empty-state card">No events match your search yet. Try a different keyword or check back soon.</div>
        ) : (
          events.map((event) => <EventTicketCard key={event.id} event={event} />)
        )}
      </div>
    </div>
  )
}
