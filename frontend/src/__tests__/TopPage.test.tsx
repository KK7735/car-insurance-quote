import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect } from 'vitest';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { TopPage } from '../pages/TopPage';

describe('TopPage', () => {
  it('ST-001: should navigate to step1 when start button is clicked', async () => {
    const user = userEvent.setup();
    render(
      <MemoryRouter initialEntries={['/']}>
        <Routes>
          <Route path="/" element={<TopPage />} />
          <Route path="/quote/step1" element={<div data-testid="step1">Step 1</div>} />
        </Routes>
      </MemoryRouter>
    );

    const startButton = screen.getByText('見積もりを始める');
    await user.click(startButton);

    expect(screen.getByTestId('step1')).toBeInTheDocument();
  });
});
