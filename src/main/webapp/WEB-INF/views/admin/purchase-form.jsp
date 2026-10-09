<c:set var="pageTitle" value="New Purchase" />
<c:set var="activeNav" value="purchases" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card">
  <div class="card-header">Record a purchase (stock is increased when you save)</div>
  <div class="card-body">
    <form method="post" action="${ctx}/admin/purchases" class="needs-validation" novalidate>
      <div class="row g-3 mb-3">
        <div class="col-md-6">
          <label class="form-label" for="supplierId">Supplier *</label>
          <select class="form-select" id="supplierId" name="supplierId" required>
            <option value="">Select supplier</option>
            <c:forEach var="s" items="${suppliers}"><option value="${s.id}"><c:out value="${s.supplierName}" /></option></c:forEach>
          </select>
          <div class="invalid-feedback">Please select a supplier.</div>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="purchaseDate">Purchase date *</label>
          <input type="date" class="form-control" id="purchaseDate" name="purchaseDate" value="${today}" max="${today}" required>
          <div class="invalid-feedback">Purchase date is required.</div>
        </div>
      </div>

      <div class="table-responsive">
        <table class="table align-middle" id="purchaseTable">
          <thead><tr><th>Medicine</th><th style="width:9rem">Quantity</th><th style="width:11rem">Purchase price (&#8377;)</th><th class="text-end" style="width:9rem">Line total</th><th style="width:3rem"></th></tr></thead>
          <tbody id="purchaseRows">
            <tr class="purchase-row">
              <td>
                <select class="form-select p-med" name="medicineId" required>
                  <option value="">Select medicine</option>
                  <c:forEach var="m" items="${medicines}"><option value="${m.id}" data-price="${m.purchasePrice}"><c:out value="${m.medicineName}" /> - batch <c:out value="${m.batchNumber}" /></option></c:forEach>
                </select>
              </td>
              <td><input type="number" class="form-control p-qty" name="quantity" min="1" step="1" value="1" required></td>
              <td><input type="number" class="form-control p-price" name="purchasePrice" min="0" step="0.01" required></td>
              <td class="text-end p-total">0.00</td>
              <td><button type="button" class="btn btn-sm btn-outline-danger p-remove" title="Remove row"><i class="bi bi-x-lg"></i></button></td>
            </tr>
          </tbody>
          <tfoot><tr><th colspan="3" class="text-end">Total</th><th class="text-end" id="purchaseGrand">0.00</th><th></th></tr></tfoot>
        </table>
      </div>
      <button type="button" class="btn btn-outline-primary btn-sm mb-3" id="addPurchaseRow"><i class="bi bi-plus-lg me-1"></i>Add another medicine</button>
      <div class="d-flex gap-2">
        <button class="btn btn-primary" type="submit"><i class="bi bi-save me-1"></i>Save purchase</button>
        <a class="btn btn-light" href="${ctx}/admin/purchases">Cancel</a>
      </div>
    </form>
  </div>
</div>
<script>
  (function () {
    var body = document.getElementById('purchaseRows');
    function recalc() {
      var grand = 0;
      body.querySelectorAll('.purchase-row').forEach(function (r) {
        var t = (parseFloat(r.querySelector('.p-qty').value) || 0) * (parseFloat(r.querySelector('.p-price').value) || 0);
        r.querySelector('.p-total').textContent = t.toFixed(2);
        grand += t;
      });
      document.getElementById('purchaseGrand').textContent = grand.toFixed(2);
    }
    body.addEventListener('input', recalc);
    body.addEventListener('change', function (e) {
      if (e.target.classList.contains('p-med')) {                 // prefill last purchase price
        var opt = e.target.selectedOptions[0], price = opt ? opt.getAttribute('data-price') : '';
        var input = e.target.closest('tr').querySelector('.p-price');
        if (price && !input.value) input.value = price;
      }
      recalc();
    });
    body.addEventListener('click', function (e) {
      var btn = e.target.closest('.p-remove');
      if (btn && body.querySelectorAll('.purchase-row').length > 1) { btn.closest('tr').remove(); recalc(); }
    });
    document.getElementById('addPurchaseRow').addEventListener('click', function () {
      var clone = body.querySelector('.purchase-row').cloneNode(true);
      clone.querySelector('.p-med').value = '';
      clone.querySelector('.p-qty').value = 1;
      clone.querySelector('.p-price').value = '';
      body.appendChild(clone);
      recalc();
    });
  })();
</script>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
