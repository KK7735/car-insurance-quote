import React from 'react';
import { useFormContext } from 'react-hook-form';

interface FormRadioGroupProps {
  name: string;
  label: string;
  options: { value: string | boolean; label: string }[];
}

export const FormRadioGroup: React.FC<FormRadioGroupProps> = ({ name, label, options }) => {
  const { register, formState: { errors } } = useFormContext();
  const error = errors[name]?.message as string;

  return (
    <div className="form-group">
      <label className="form-label">{label}</label>
      <div className="radio-group">
        {options.map((opt) => (
          <label key={opt.value.toString()} className="radio-label">
            <input
              type="radio"
              value={opt.value.toString()}
              className="radio-input"
              {...register(name, {
                 setValueAs: (v) => {
                   if (v === 'true') return true;
                   if (v === 'false') return false;
                   return v;
                 }
              })}
            />
            {opt.label}
          </label>
        ))}
      </div>
      {error && <span className="form-error">{error}</span>}
    </div>
  );
};
