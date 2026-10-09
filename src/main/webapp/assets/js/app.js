/* PharmaCare front-end helpers (Bootstrap 5, no other libraries) */
document.addEventListener('DOMContentLoaded', function () {

  // ---- sidebar toggle (mobile) ----
  var sidebar = document.getElementById('sidebar');
  var backdrop = document.getElementById('sidebarBackdrop');
  var toggle = document.getElementById('sidebarToggle');
  function closeSidebar() { sidebar.classList.remove('open'); backdrop.classList.remove('show'); }
  if (toggle && sidebar) {
    toggle.addEventListener('click', function () { sidebar.classList.toggle('open'); backdrop.classList.toggle('show'); });
    backdrop.addEventListener('click', closeSidebar);
  }

  // ---- toasts ----
  document.querySelectorAll('.js-auto-toast').forEach(function (el) { new bootstrap.Toast(el, { delay: 4500 }).show(); });

  // ---- client-side validation (server validates again) ----
  document.querySelectorAll('form.needs-validation').forEach(function (form) {
    var pw = form.querySelector('#password');
    var cf = form.querySelector('#confirmPassword');
    function matchCheck() {
      if (pw && cf) cf.setCustomValidity(cf.value !== pw.value ? 'Passwords do not match' : '');
    }
    if (pw && cf) { pw.addEventListener('input', matchCheck); cf.addEventListener('input', matchCheck); }
    form.addEventListener('submit', function (e) {
      matchCheck();
      if (!form.checkValidity()) { e.preventDefault(); e.stopPropagation(); }
      form.classList.add('was-validated');
    });
  });

  // ---- add/edit modals: copy data-* attributes of the clicked button into the form ----
  // e.g. data-customer-name="Bob" fills <input name="customerName">
  document.querySelectorAll('.crud-modal').forEach(function (modal) {
    modal.addEventListener('show.bs.modal', function (event) {
      var btn = event.relatedTarget;
      var form = modal.querySelector('form');
      form.reset();
      form.classList.remove('was-validated');
      form.querySelectorAll('[name]').forEach(function (field) {
        if (field.type === 'hidden' && field.name !== 'id') return;      // keep action / returnTo
        var value = btn && btn.dataset ? btn.dataset[field.name] : undefined;
        if (value !== undefined) field.value = value;
        else if (field.name === 'id') field.value = '0';
      });
      var title = modal.querySelector('.modal-title');
      if (title && btn && btn.dataset.modalTitle) title.textContent = btn.dataset.modalTitle;
    });
  });

  // ---- delete confirmation modal ----
  var deleteModalEl = document.getElementById('deleteModal');
  if (deleteModalEl) {
    var deleteModal = new bootstrap.Modal(deleteModalEl);
    document.addEventListener('click', function (e) {
      var btn = e.target.closest('[data-delete-url]');
      if (!btn) return;
      document.getElementById('deleteForm').action = btn.dataset.deleteUrl;
      document.getElementById('deleteId').value = btn.dataset.deleteId;
      document.getElementById('deleteName').textContent = btn.dataset.deleteName || 'this record';
      deleteModal.show();
    });
  }

  // ---- billing: live totals (the server recalculates everything on save) ----
  var cartForm = document.getElementById('cartForm');
  if (cartForm) {
    var gstRate = parseFloat(cartForm.dataset.gstRate) || 0;
    var money = function (n) { return n.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }); };
    var recalc = function () {
      var subtotal = 0;
      cartForm.querySelectorAll('.cart-row').forEach(function (row) {
        var line = (parseFloat(row.dataset.price) || 0) * (parseInt(row.querySelector('.cart-qty').value, 10) || 0);
        row.querySelector('.line-total').textContent = '\u20B9' + money(line);
        subtotal += line;
      });
      var pct = Math.min(Math.max(parseFloat(document.getElementById('discountPercent').value) || 0, 0), 100);
      var discount = Math.round(subtotal * pct) / 100;
      var gst = Math.round((subtotal - discount) * gstRate) / 100;
      document.getElementById('tSubtotal').textContent = money(subtotal);
      document.getElementById('tDiscount').textContent = money(discount);
      document.getElementById('tGst').textContent = money(gst);
      document.getElementById('tGrand').textContent = money(subtotal - discount + gst);
    };
    cartForm.addEventListener('input', recalc);
  }

  // ---- auto-open the print dialog for invoice links that ask for it ----
  if (document.querySelector('.invoice') && /[?&]print=1/.test(window.location.search)) {
    setTimeout(function () { window.print(); }, 400);
  }
});
