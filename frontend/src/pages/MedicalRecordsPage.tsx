import React, { useState } from 'react';
import { MedicalRecord } from '../types';
import { Modal } from '../components/Modal';

export const MedicalRecordsPage: React.FC = () => {
  const [records, setRecords] = useState<MedicalRecord[]>([
    {
      record_id: 1,
      patient: 1,
      patient_name: 'Eleanor Vance',
      doctor: 1,
      doctor_name: 'Dr. Aris Thorne',
      diagnosis: 'Stage 1 Essential Hypertension',
      notes: 'Patient advised low sodium diet and 30 min cardiovascular exercise daily. Started on Lisinopril 10mg once daily.',
      record_date: '2026-09-10'
    },
    {
      record_id: 2,
      patient: 2,
      patient_name: 'David Miller',
      doctor: 3,
      doctor_name: 'Dr. Marcus Chen',
      diagnosis: 'Episodic Migraine without Aura',
      notes: 'Triggers include sleep irregularity. Prescribed Sumatriptan 50mg PRN for acute episodes. Keep headache diary.',
      record_date: '2026-09-12'
    }
  ]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [form, setForm] = useState({
    patient_name: 'Eleanor Vance',
    doctor_name: 'Dr. Aris Thorne (Cardiology)',
    diagnosis: '',
    notes: '',
    record_date: new Date().toISOString().split('T')[0]
  });

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    const newId = records.length + 1;
    setRecords([
      {
        record_id: newId,
        patient: 1,
        patient_name: form.patient_name,
        doctor: 1,
        doctor_name: 'Dr. Aris Thorne',
        diagnosis: form.diagnosis,
        notes: form.notes,
        record_date: form.record_date
      },
      ...records
    ]);
    setIsModalOpen(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Clinical Medical Records & Diagnoses</h1>
          <p className="text-sm text-slate-500">Document clinical encounter diagnoses, treatment plans, and medical history</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold px-4 py-2.5 rounded-xl shadow-sm transition-colors"
        >
          + Record Clinical Encounter
        </button>
      </div>

      <div className="space-y-4">
        {records.map(r => (
          <div key={r.record_id} className="bg-white rounded-xl border border-slate-200 p-5 shadow-xs">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center border-b border-slate-100 pb-3 mb-3 gap-2">
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-blue-600">Encounter Record #{r.record_id}</span>
                <h3 className="font-bold text-slate-900 text-base">{r.diagnosis}</h3>
              </div>
              <div className="text-xs text-slate-400">
                Date: <span className="font-medium text-slate-700">{r.record_date}</span>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs mb-3">
              <div><span className="text-slate-400">Patient:</span> <span className="font-semibold text-slate-800">{r.patient_name}</span></div>
              <div><span className="text-slate-400">Attending Physician:</span> <span className="font-semibold text-slate-800">{r.doctor_name}</span></div>
            </div>

            <div className="bg-slate-50 rounded-lg p-3 text-xs text-slate-700 font-mono">
              <span className="font-bold block mb-1 font-sans text-slate-500">Treatment Plan & Clinical Notes:</span>
              {r.notes}
            </div>
          </div>
        ))}
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Add Clinical Encounter Documentation">
        <form onSubmit={handleCreate} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Patient *</label>
            <select
              value={form.patient_name}
              onChange={(e) => setForm({ ...form, patient_name: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            >
              <option value="Eleanor Vance">Eleanor Vance (#1)</option>
              <option value="David Miller">David Miller (#2)</option>
            </select>
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Diagnosis *</label>
            <input
              type="text"
              required
              placeholder="e.g. Acute Bronchitis, Type 2 Diabetes"
              value={form.diagnosis}
              onChange={(e) => setForm({ ...form, diagnosis: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Clinical Notes & Treatment Plan *</label>
            <textarea
              required
              rows={4}
              placeholder="Symptoms, findings, medications prescribed, follow-up instructions..."
              value={form.notes}
              onChange={(e) => setForm({ ...form, notes: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div className="flex justify-end gap-2 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-lg"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-xs font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-lg shadow-sm"
            >
              Save Record
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
