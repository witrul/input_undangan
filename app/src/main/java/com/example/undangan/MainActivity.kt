class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViews()
        setupListeners()
    }
    
    private fun setupViews() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioManual -> {
                    binding.manualInputLayout.visibility = View.VISIBLE
                    binding.contactInputLayout.visibility = View.GONE
                }
                R.id.radioContact -> {
                    binding.manualInputLayout.visibility = View.GONE
                    binding.contactInputLayout.visibility = View.VISIBLE
                }
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnSubmit.setOnClickListener {
            val nama = binding.edtNama.text.toString()
            val whatsapp = if (binding.radioManual.isChecked) {
                binding.edtWhatsapp.text.toString()
            } else {
                binding.edtContactWhatsapp.text.toString()
            }
            
            if (nama.isNotEmpty() && whatsapp.isNotEmpty()) {
                generateUndangan(nama, whatsapp)
            }
        }
        
        binding.btnPilihKontak.setOnClickListener {
            pickContact()
        }
    }
    
    private fun generateUndangan(nama: String, whatsapp: String) {
        val undanganUrl = "https://yourdomain.com/index.php?to=${Uri.encode(nama)}"
        val pesanWA = """
            Assalamualaikum Wr. Wb.
            
            Kepada Yth. $nama
            
            Tanpa mengurangi rasa hormat, perkenankan kami mengundang Bapak/Ibu/Saudara/i untuk menghadiri acara pernikahan kami.
            
            Hari/Tanggal: 08 Februari 2025
            Jam: 08:00 WIB - Selesai
            Tempat: Jl. Kh Ma'ruf Rt. 014 Rw.003 Jatisari Tajinan - Malang
            
            Kami sangat mengharapkan kehadiran bapak/ibu/saudara/i untuk memberikan do'a restu dalam acara pernikahan kami.
            
            Silahkan klik link berikut untuk membuka undangan digital kami:
            
            $undanganUrl
            
            Terima kasih
            
            Wassalamualaikum Wr. Wb.
            
            *Fidia & Sigit*
        """.trimIndent()
        
        val waUrl = "https://wa.me/${whatsapp.replace(Regex("[^0-9]"), "")}?text=${Uri.encode(pesanWA)}"
        
        // Tampilkan hasil
        showResult(undanganUrl, waUrl)
    }
    
    private fun showResult(undanganUrl: String, waUrl: String) {
        binding.resultLayout.visibility = View.VISIBLE
        binding.txtUndanganUrl.setText(undanganUrl)
        
        binding.btnCopyUrl.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Undangan URL", undanganUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "URL berhasil disalin!", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnShareWa.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
            startActivity(intent)
        }
    }
    
    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(intent, PICK_CONTACT)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            val contactUri = data?.data ?: return
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            contentResolver.query(contactUri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val number = cursor.getString(numberIndex)
                    binding.edtContactWhatsapp.setText(number)
                }
            }
        }
    }
    
    companion object {
        private const val PICK_CONTACT = 1
    }
} 