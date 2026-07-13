import React from 'react'
import { Link } from 'react-router-dom'

const MONTHS = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC']

export default function EventTicketCard({ event, children }) {
  const date = new Date(event.eventDate)
  const month = MONTHS[date.getMonth()]
  const day = date.getDate()
  const time = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  const spotsLeft = event.capacity != null ? event.capacity - event.registeredCount : null

  return (
    <div className="ticket">
      <div className="ticket-stub">
        <span className="month">{month}</span>
        <span className="day">{day}</span>
        <span className="time">{time}</span>
      </div>
      <div className="ticket-perforation" />
      <div className="ticket-body">
        {event.category && <span className="ticket-tag">{event.category}</span>}
        <h3><Link to={`/events/${event.id}`}>{event.title}</Link></h3>
        <div className="ticket-meta">
          <span>📍 {event.venue}</span>
          {spotsLeft !== null && (
            <span className="capacity-note">
              {spotsLeft > 0 ? `${spotsLeft} spot${spotsLeft === 1 ? '' : 's'} left` : 'Fully booked'}
            </span>
          )}
        </div>
        {event.speakers && event.speakers.length > 0 && (
          <div>
            {event.speakers.map((s) => (
              <span className="speaker-chip" key={s.id}>🎤 {s.name}</span>
            ))}
          </div>
        )}
        {children && <div className="ticket-actions">{children}</div>}
      </div>
    </div>
  )
}
