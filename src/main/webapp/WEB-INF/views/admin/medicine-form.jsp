<c:set var="isEdit" value="${medicine.id > 0}" />
<c:set var="pageTitle" value="${isEdit ? 'Edit Medicine' : 'Add Medicine'}" />
<c:set var="activeNav" value="medicines" />
<c:set var="medicineBase" value="${empty medicineBase ? 'admin' : medicineBase}" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="card">
  <div class="card-header"><c:out value="${pageTitle}" /></div>
  <div class="card-body">
    <form method="post" action="${ctx}/${medicineBase}/medicines" class="needs-validation" novalidate>
      <input type="hidden" name="action" value="save">
      <input type="hidden" name="id" value="${medicine.id}">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label" for="medicineName">Medicine name *</label>
          <input class="form-control" id="medicineName" name="medicineName" value="<c:out value='${medicine.medicineName}'/>" required maxlength="150">
          <div class="invalid-feedback">Medicine name is required.</div>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="genericName">Generic name</label>
          <input class="form-control" id="genericName" name="genericName" value="<c:out value='${medicine.genericName}'/>" maxlength="150">
        </div>
        <div class="col-md-4">
          <label class="form-label" for="categoryId">Category *</label>
          <select class="form-select" id="categoryId" name="categoryId" required>
            <option value="">Select category</option>
            <c:forEach var="c" items="${categories}"><option value="${c.id}" ${medicine.categoryId == c.id ? 'selected' : ''}><c:out value="${c.categoryName}" /></option></c:forEach>
          </select>
          <div class="invalid-feedback">Please select a category.</div>
        </div>
        <div class="col-md-4">
          <label class="form-label" for="supplierId">Supplier *</label>
          <select class="form-select" id="supplierId" name="supplierId" required>
            <option value="">Select supplier</option>
            <c:forEach var="s" items="${suppliers}"><option value="${s.id}" ${medicine.supplierId == s.id ? 'selected' : ''}><c:out value="${s.supplierName}" /></option></c:forEach>
          </select>
          <div class="invalid-feedback">Please select a supplier.</div>
        </div>
        <div class="col-md-4">
          <label class="form-label" for="batchNumber">Batch number *</label>
          <input class="form-control" id="batchNumber" name="batchNumber" value="<c:out value='${medicine.batchNumber}'/>" required maxlength="50">
          <div class="invalid-feedback">Batch number is required.</div>
        </div>
        <div class="col-md-3">
          <label class="form-label" for="manufactureDate">Manufacture date *</label>
          <input type="date" class="form-control" id="manufactureDate" name="manufactureDate" value="${medicine.manufactureDate}" required>
          <div class="invalid-feedback">Manufacture date is required.</div>
        </div>
        <div class="col-md-3">
          <label class="form-label" for="expiryDate">Expiry date *</label>
          <input type="date" class="form-control" id="expiryDate" name="expiryDate" value="${medicine.expiryDate}" required>
          <div class="invalid-feedback">Expiry must be after the manufacture date.</div>
        </div>
        <div class="col-md-3">
          <label class="form-label" for="purchasePrice">Purchase price (&#8377;) *</label>
          <input type="number" step="0.01" min="0" max="99999999.99" class="form-control" id="purchasePrice" name="purchasePrice" value="${medicine.purchasePrice}" required>
          <div class="invalid-feedback">Enter a price of 0 or more.</div>
        </div>
        <div class="col-md-3">
          <label class="form-label" for="sellingPrice">Selling price (&#8377;) *</label>
          <input type="number" step="0.01" min="0" max="99999999.99" class="form-control" id="sellingPrice" name="sellingPrice" value="${medicine.sellingPrice}" required>
          <div class="invalid-feedback">Enter a price of 0 or more.</div>
        </div>
        <div class="col-md-3">
          <label class="form-label" for="stockQuantity">Stock quantity *</label>
          <input type="number" step="1" min="0" class="form-control" id="stockQuantity" name="stockQuantity" value="${medicine.stockQuantity}" required>
          <div class="invalid-feedback">Stock cannot be negative.</div>
        </div>
        <div class="col-md-3">
          <label class="form-label" for="reorderLevel">Reorder level *</label>
          <input type="number" step="1" min="0" class="form-control" id="reorderLevel" name="reorderLevel" value="${medicine.reorderLevel}" required>
          <div class="invalid-feedback">Reorder level cannot be negative.</div>
        </div>
        <div class="col-12">
          <label class="form-label" for="description">Description</label>
          <textarea class="form-control" id="description" name="description" rows="2" maxlength="500"><c:out value="${medicine.description}" /></textarea>
        </div>
      </div>
      <div class="mt-4 d-flex gap-2">
        <button class="btn btn-primary" type="submit"><i class="bi bi-save me-1"></i>${isEdit ? 'Update' : 'Save'} medicine</button>
        <a class="btn btn-light" href="${ctx}/${medicineBase}/medicines">Cancel</a>
      </div>
    </form>
  </div>
</div>
<script>
  // client-side check: expiry must be after manufacture date (server validates again)
  (function () {
    var mfg = document.getElementById('manufactureDate'), exp = document.getElementById('expiryDate');
    function check() {
      exp.setCustomValidity(mfg.value && exp.value && exp.value <= mfg.value ? 'Expiry must be after manufacture date' : '');
    }
    mfg.addEventListener('change', check); exp.addEventListener('change', check); check();
  })();
</script>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
